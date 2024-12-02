package pretzel.dreamketcherbe.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.auth.dto.TokenResponse;
import pretzel.dreamketcherbe.auth.entity.Token;
import pretzel.dreamketcherbe.auth.exception.AuthException;
import pretzel.dreamketcherbe.auth.exception.AuthExceptionType;
import pretzel.dreamketcherbe.auth.google.dto.GoogleUserInfo;
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
    private final TokenProvider tokenprovider;
    private final TokenRepository tokenRepository;

    @Transactional
    public TokenResponse loginOrRegister(GoogleUserInfo googleUserInfo) {
        Member member = memberRepository.findBySocialId(googleUserInfo.socialId())
            .orElseGet(() -> memberRepository.save(googleUserInfo.toMember()));
        return createToken(member.getId());
    }

    private TokenResponse createToken(Long memberId) {
        Token token = new Token(memberId);
        tokenRepository.save(token);
        return generatedTokenPair(token);
    }

    private TokenResponse generatedTokenPair(Token token) {
        String accessToken = tokenprovider.generatedAccessToken(token.getMemberId());
        String refreshToken = tokenprovider.generatedRefreshToken(token.getTokenId());
        return TokenResponse.of(accessToken, refreshToken);
    }

    public TokenResponse refreshTokenById(String tokenId) {
        Token token = tokenRepository.findByTokenId(tokenId)
            .orElseThrow(() -> new AuthException(AuthExceptionType.INVALID_TOKEN));
        tokenRepository.deleteByTokenId(token.getTokenId());
        return createToken(token.getMemberId());
    }
}
