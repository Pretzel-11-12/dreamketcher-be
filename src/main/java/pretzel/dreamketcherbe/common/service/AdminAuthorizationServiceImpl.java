package pretzel.dreamketcherbe.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class AdminAuthorizationServiceImpl implements AdminAuthorizationService {

    private final MemberRepository memberRepository;

    @Override
    public boolean isAdmin(Long memberId) {
        return memberRepository.findById(memberId)
            .map(member -> "ADMIN".equals(member.getRole()))
            .orElse(false);
    }

}
