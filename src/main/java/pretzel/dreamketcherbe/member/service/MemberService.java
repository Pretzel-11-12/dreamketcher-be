package pretzel.dreamketcherbe.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.member.dto.SelfInfoResponse;
import pretzel.dreamketcherbe.member.entity.Member;
import pretzel.dreamketcherbe.member.exception.MemberException;
import pretzel.dreamketcherbe.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.member.repository.MemberRepository;

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
}
