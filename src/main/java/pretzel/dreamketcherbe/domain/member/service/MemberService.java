package pretzel.dreamketcherbe.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.member.dto.NicknameRequest;
import pretzel.dreamketcherbe.domain.member.dto.SelfInfoResponse;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public SelfInfoResponse getSelfInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        return SelfInfoResponse.of(member);
    }

    public Object updateProfile(Long memberId, NicknameRequest nicknameRequest) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        if (memberRepository.existsByNickname(nicknameRequest.nickname())) {
            throw new MemberException(MemberExceptionType.NICKNAME_ALREADY_EXISTS);
        }

        member.updateName(nicknameRequest.nickname());
        return memberRepository.save(member);
    }
}