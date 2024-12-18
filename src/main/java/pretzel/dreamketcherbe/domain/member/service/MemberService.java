package pretzel.dreamketcherbe.domain.member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.member.dto.InterestedWebtoonResponse;
import pretzel.dreamketcherbe.domain.member.dto.NicknameRequest;
import pretzel.dreamketcherbe.domain.member.dto.SelfInfoResponse;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.InterestedWebtoonRepository;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;

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

        member.updateNickname(nicknameRequest.nickname());

        return memberRepository.save(member);
    }

    public List<InterestedWebtoonResponse> getFavoriteWebtoon(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        List<InterestedWebtoon> favoriteWebtoons = interestedWebtoonRepository.findAllByMemberId(
            member);

        return favoriteWebtoons.stream()
            .map(InterestedWebtoonResponse::from)
            .toList();
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Transactional
    public void deleteFavoriteWebtoon(Long memberId, Long interestedWebtoonId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        InterestedWebtoon interestedWebtoon = interestedWebtoonRepository.findById(
                interestedWebtoonId)
            .orElseThrow(
                () -> new MemberException(MemberExceptionType.INTERESTED_WEBTOON_NOT_FOUND));

        if (!interestedWebtoon.getMember().equals(member)) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_AUTHORIZED);
        }

        interestedWebtoonRepository.delete(interestedWebtoon);
    }
}