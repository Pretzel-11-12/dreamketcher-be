package pretzel.dreamketcherbe.domain.member.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pretzel.dreamketcherbe.S3Utils.S3Service;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.domain.member.dto.InterestedWebtoonResDto;
import pretzel.dreamketcherbe.domain.member.dto.MemberInfoResDto;
import pretzel.dreamketcherbe.domain.member.dto.UpdateProfileReqDto;
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
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final InterestedWebtoonRepository interestedWebtoonRepository;
    private final WebtoonRepository webtoonRepository;
    private final S3Service s3Service;

    @Value("${default.profile.image.url}")
    private String defaultProfileImageUrl;

    public MemberInfoResDto getSelfInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        return MemberInfoResDto.ofSelf(member);
    }


    public MemberInfoResDto getMemberInfo(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));
        return MemberInfoResDto.ofProfile(member);
    }

    private void updateBusinessEmail(Member member, String newBusinessEmail, Long memberId) {
        Optional.ofNullable(newBusinessEmail)

            .map(String::trim)
            .ifPresent(email -> {
                if (email.isEmpty()) {
                    member.updateBusinessEmail("");
                } else if (!email.equals(member.getBusinessEmail())) {
                    if (memberRepository.existsByBusinessEmailAndIdNot(email, memberId)) {
                        throw new MemberException(
                            MemberExceptionType.BUSINESS_EMAIL_ALREADY_EXISTS);
                    }
                    member.updateBusinessEmail(email);
                }
            });
    }

    private void updateNickname(Member member, String newNickname, Long memberId) {
        Optional.ofNullable(newNickname)
            .filter(nickname -> !nickname.equals(member.getNickname()))
            .ifPresent(nickname -> {
                if (memberRepository.existsByNicknameAndIdNot(nickname, memberId)) {
                    throw new MemberException(MemberExceptionType.NICKNAME_ALREADY_EXISTS);
                }
                member.updateNickname(nickname);
            });
    }

    private void updateShortIntroduction(Member member, String newShortIntroduction) {
        Optional.ofNullable(newShortIntroduction)
            .map(String::trim)
            .ifPresentOrElse(
                shortIntro -> {
                    if (!shortIntro.equals(member.getShortIntroduction())) {
                        member.updateShortIntroduction(shortIntro);
                    }
                },
                () -> {
                    // newShortIntroduction이 null인 경우 아무런 변경도 하지 않음
                }
            );

        if (newShortIntroduction != null && newShortIntroduction.isBlank()) {
            member.updateShortIntroduction("");
        }
    }

    // NOTE: s3Service 메서드 와 Transactional 관계 확인
    @Transactional
    public void updateProfileWithImage(Long memberId, MultipartFile image,
        UpdateProfileReqDto profileData) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        if (profileData.businessEmail() != null || profileData.nickname() != null
            || profileData.shortIntroduction() != null) {
            String newNickname = profileData.nickname();
            String newBusinessEmail = profileData.businessEmail();
            String newShortIntroduction = profileData.shortIntroduction();

            updateNickname(member, newNickname, memberId);
            updateBusinessEmail(member, newBusinessEmail, memberId);
            updateShortIntroduction(member, newShortIntroduction);
        }

        if (profileData.isDeleteImage()) {
            if (member.getImageUrl() != null && !member.getImageUrl()
                .equals(defaultProfileImageUrl)) {
                s3Service.deleteImage(member.getImageUrl());
            }
            member.updateImageUrl(defaultProfileImageUrl);
        } else {
            Optional.ofNullable(image)
                .filter(img -> !img.isEmpty())
                .ifPresent(img -> {
                    String folderName = "profile-images/" + memberId;
                    String currentImageUrl = member.getImageUrl();
                    String newImageUrl;

                    if (!currentImageUrl.equals(defaultProfileImageUrl)) {
                        try {
                            newImageUrl = s3Service.imageUpdate(currentImageUrl, img, folderName);
                        } catch (Exception e) {
                            // 기존 이미지 경로가 defaultProfileImageUrl인 경우
                            newImageUrl = s3Service.imageUpload(img, folderName);
                        }
                    } else {
                        newImageUrl = s3Service.imageUpload(img, folderName);
                    }
                    member.updateImageUrl(newImageUrl);
                });
        }
        memberRepository.save(member);
    }

    public boolean isFavoriteWebtoon(Long memberId, Long webtoonId) {
        Webtoon webtoon = webtoonRepository.findById(webtoonId)
            .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));

        interestedWebtoonRepository.findByWebtoonIdAndMemberId(webtoonId, memberId);
        return interestedWebtoonRepository.findByWebtoonIdAndMemberId(webtoonId, memberId)
            .isPresent();
    }

    public List<InterestedWebtoonResDto> getAllFavoriteWebtoon(Long memberId) {

        List<InterestedWebtoon> favoriteWebtoons = interestedWebtoonRepository.findAllByMemberId(
            memberId);

        return favoriteWebtoons.stream()
            .map(interestedWebtoon -> {
                Webtoon webtoon = interestedWebtoon.getWebtoon();
                Member author = webtoon.getMember();

                return InterestedWebtoonResDto.from(
                    interestedWebtoon,
                    author.getNickname(),
                    webtoon.getEpisodeCount(),
                    webtoon.getUpdatedAt(),
                    webtoon.getGenre().getName()
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
    public WorkResDto getAllWorksByMemberId(final Long memberId, final String status,
        final PageReqDto pageReqDto) {
        return memberRepository.findAllWorkWithPage(memberId, status, pageReqDto);
    }

    @Transactional(readOnly = true)
    public WorkResDto getAllWorksByNickname(String nickname, final String status,
        final PageReqDto pageReqDto) {
        Member member = memberRepository.findByNickname(nickname)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));
        return memberRepository.findAllWorkWithPageWithStatus(member.getId(), status, pageReqDto);
    }
}