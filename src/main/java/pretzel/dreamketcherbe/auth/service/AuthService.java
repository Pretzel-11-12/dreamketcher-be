package pretzel.dreamketcherbe.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.auth.dto.RenewAccessTokenResponse;
import pretzel.dreamketcherbe.auth.dto.TokenResponse;
import pretzel.dreamketcherbe.auth.entity.Token;
import pretzel.dreamketcherbe.auth.exception.AuthException;
import pretzel.dreamketcherbe.auth.exception.AuthExceptionType;
import pretzel.dreamketcherbe.auth.google.GoogleOAuthClient;
import pretzel.dreamketcherbe.auth.google.dto.GoogleUserInfo;
import pretzel.dreamketcherbe.auth.repository.TokenExtractor;
import pretzel.dreamketcherbe.auth.repository.TokenProvider;
import pretzel.dreamketcherbe.auth.repository.TokenRepository;
import pretzel.dreamketcherbe.member.entity.Member;
import pretzel.dreamketcherbe.member.repository.MemberRepository;

import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final GoogleOAuthClient googleOAuthClient;
    private final TokenProvider tokenprovider;
    private final TokenExtractor tokenExtractor;
    private final TokenRepository tokenRepository;

    @Transactional
    public TokenResponse loginOrRegister(String SocialType, String code) {

        GoogleUserInfo googleUserInfo = googleOAuthClient.getOAuthInfo(code);

        log.info("googleUserInfo: {}", googleUserInfo);
        Member member = memberRepository.findBySocialId(SocialType)
            .orElseGet(() -> memberRepository.save(googleUserInfo.toMember()));

        log.info("member: {}", member);
        String tokenId = UUID.randomUUID().toString();
        Token token = new Token(member.getId(), tokenId);

        tokenRepository.save(token);

        return generatedTokenPair(token);
    }

    public TokenResponse renewAccessToken(String refreshToken) {
        String tokenId = tokenExtractor.extractRefreshToken(refreshToken);

        Token token = tokenRepository.findByTokenId(tokenId)
            .orElseThrow(() -> new AuthException(AuthExceptionType.INVALID_TOKEN));

        String newTokenId = UUID.randomUUID().toString();
        token.updateTokenId(newTokenId);

        return generatedTokenPair(token);
    }

    private TokenResponse generatedTokenPair(Token token) {
        String accessToken = tokenprovider.generatedAccessToken(token.getMemberId());
        String refreshToken = tokenprovider.generatedRefreshToken(token.getTokenId());

        return TokenResponse.of(accessToken, refreshToken);
    }
}
