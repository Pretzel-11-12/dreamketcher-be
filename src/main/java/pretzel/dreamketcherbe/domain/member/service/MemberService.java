package pretzel.dreamketcherbe.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.member.dto.FavoriteWebtoonResponse;
import pretzel.dreamketcherbe.domain.member.dto.NicknameRequest;
import pretzel.dreamketcherbe.domain.member.dto.SelfInfoResponse;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.InterestedWebtoonRepository;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final InterestedWebtoonRepository interestedWebtoonRepository;

    public SelfInfoResponse getSelfInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        return SelfInfoResponse.of(member);
    }

    @Transactional
    public Object updateProfile(Long memberId, NicknameRequest nicknameRequest) {
        Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        if (memberRepository.existsByNickname(nicknameRequest.nickname())) {
            throw new MemberException(MemberExceptionType.NICKNAME_ALREADY_EXISTS);
        }

        member.updateProfile(nicknameRequest.nickname());

        return memberRepository.save(member);
    }

    public List<FavoriteWebtoonResponse> getFavoriteWebtoon(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        List<InterestedWebtoon> favoriteWebtoons = interestedWebtoonRepository.findAllByMemberId(member);

        return favoriteWebtoons.stream()
                    .map(FavoriteWebtoonResponse::from)
                    .toList();
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Transactional
    public void deleteFavoriteWebtoon(Long memberId, Long interestedWebtoonId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        InterestedWebtoon interestedWebtoon = interestedWebtoonRepository.findById(interestedWebtoonId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.INTERESTED_WEBTOON_NOT_FOUND));

        if (!interestedWebtoon.getMemberId().equals(member)) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_AUTHORIZED);
        }

        interestedWebtoonRepository.delete(interestedWebtoon);
    }
}