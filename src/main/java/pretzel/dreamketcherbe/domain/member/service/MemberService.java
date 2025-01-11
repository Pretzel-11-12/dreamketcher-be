package pretzel.dreamketcherbe.domain.member.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pretzel.dreamketcherbe.S3Utils.S3Service;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.member.dto.InterestedWebtoonResponse;
import pretzel.dreamketcherbe.domain.member.dto.InterestedWebtoonSimpleResponse;
import pretzel.dreamketcherbe.domain.member.dto.SelfInfoResponse;
import pretzel.dreamketcherbe.domain.member.dto.UpdateProfileRequest;
import pretzel.dreamketcherbe.domain.member.dto.WorkResDto;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.InterestedWebtoonRepository;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonGenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final InterestedWebtoonRepository interestedWebtoonRepository;
    private final WebtoonRepository webtoonRepository;
    private final WebtoonGenreRepository webtoonGenreRepository;
    private final S3Service s3Service;

    public SelfInfoResponse getSelfInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        return SelfInfoResponse.of(member);
    }

    @Transactional
    public String uploadProfileImage(Long memberId, MultipartFile image) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        String folderName = "profile-images";
        String imageUrl = s3Service.imageUpload(image, folderName);

        member.updateImageUrl(imageUrl);
        memberRepository.save(member);

        return imageUrl;
    }

    @Transactional
    public void updateProfile(Long memberId, UpdateProfileRequest updateProfileRequest) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        String newNickname = updateProfileRequest.nickname();
        String newBusinessEmail = updateProfileRequest.businessEmail();
        String newShortIntroduction = updateProfileRequest.shortIntroduction();

        if (!newNickname.equals(member.getNickname())) {
            if (memberRepository.existsByNicknameAndIdNot(newNickname, memberId)) {
                throw new MemberException(MemberExceptionType.NICKNAME_ALREADY_EXISTS);
            }
            member.updateNickname(newNickname);
        }

        if (newBusinessEmail != null && !newBusinessEmail.isBlank()
            && !newBusinessEmail.equals(member.getBusinessEmail())) {
            if (memberRepository.existsByBusinessEmailAndIdNot(newBusinessEmail, memberId)) {
                throw new MemberException(MemberExceptionType.BUSINESS_EMAIL_ALREADY_EXISTS);
            }
            member.updateBusinessEmail(newBusinessEmail);
        }

        if (newShortIntroduction != null) {
            member.updateShortIntroduction(newShortIntroduction);
        }

        memberRepository.save(member);
    }

    public InterestedWebtoonSimpleResponse getFavoriteWebtoon(Long memberId, Long WebtoonId) {

        InterestedWebtoon interestedWebtoon = interestedWebtoonRepository.findByWebtoonIdAndMemberId(
                WebtoonId, memberId)
            .orElseThrow(
                () -> new MemberException(MemberExceptionType.INTERESTED_WEBTOON_NOT_FOUND));

        return InterestedWebtoonSimpleResponse.from(interestedWebtoon);
    }

    public List<InterestedWebtoonResponse> getAllFavoriteWebtoon(Long memberId) {

        List<InterestedWebtoon> favoriteWebtoons = interestedWebtoonRepository.findAllByMemberId(
            memberId);

        return favoriteWebtoons.stream()
            .map(interestedWebtoon -> {
                Webtoon webtoon = interestedWebtoon.getWebtoon();
                Member author = webtoon.getMember();

                List<String> genres = webtoonGenreRepository.findByWebtoon(webtoon)
                    .stream()
                    .map(WebtoonGenre -> WebtoonGenre.getGenre().getName())
                    .collect(Collectors.toList());

                return InterestedWebtoonResponse.from(
                    interestedWebtoon,
                    author.getNickname(),
                    webtoon.getEpisodeCount(),
                    webtoon.getUpdatedAt(),
                    genres
                );
            })
            .toList();
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Transactional
    public void deleteFavoriteWebtoon(Long memberId, Long WebtoonId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        InterestedWebtoon interestedWebtoon = interestedWebtoonRepository.findByWebtoonIdAndMemberId(
                WebtoonId, memberId)
            .orElseThrow(
                () -> new MemberException(MemberExceptionType.INTERESTED_WEBTOON_NOT_FOUND));

        Webtoon webtoon = webtoonRepository.findById(interestedWebtoon.getWebtoon().getId())
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        if (!interestedWebtoon.getMember().getId().equals(memberId)) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_AUTHORIZED);
        }

        interestedWebtoonRepository.delete(interestedWebtoon);

        webtoon.decrementInterestCount(1);
        webtoonRepository.save(webtoon);
    }

    @Transactional(readOnly = true)
    public PageResDto<WorkResDto> getAllWorks(final Long memberId, final String status,
        final PageReqDto pageReqDto) {
        return memberRepository.findAllWorkWithPage(memberId, status, pageReqDto);
    }
}