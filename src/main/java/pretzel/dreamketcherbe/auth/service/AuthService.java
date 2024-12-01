package pretzel.dreamketcherbe.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.auth.dto.TokenResponse;
import pretzel.dreamketcherbe.auth.google.GoogleOAuthClient;
import pretzel.dreamketcherbe.auth.google.dto.GoogleUserInfo;
import pretzel.dreamketcherbe.auth.repository.TokenProvider;
import pretzel.dreamketcherbe.member.entity.Member;
import pretzel.dreamketcherbe.member.repository.MemberRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final GoogleOAuthClient googleOAuthClient;
    private final TokenProvider tokenprovider;

    @Transactional
    public TokenResponse loginOrRegister(String SocialType, String code) {

        GoogleUserInfo googleUserInfo = googleOAuthClient.getOAuthInfo(code);

        log.info("googleUserInfo: {}", googleUserInfo);
        Member member = memberRepository.findBySocialId(SocialType)
            .orElseGet(() -> memberRepository.save(googleUserInfo.toMember()));

        log.info("member: {}", member);

        String accessToken = tokenprovider.generatedAccessToken(member.getId());
        String refreshToken = tokenprovider.generatedRefreshToken(member.getId());
        return new TokenResponse(accessToken, refreshToken);
    }
}
