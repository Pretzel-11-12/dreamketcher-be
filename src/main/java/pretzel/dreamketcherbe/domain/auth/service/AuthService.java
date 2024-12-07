package pretzel.dreamketcherbe.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.auth.dto.TokenResponse;
import pretzel.dreamketcherbe.domain.auth.entity.Token;
import pretzel.dreamketcherbe.domain.auth.exception.AuthException;
import pretzel.dreamketcherbe.domain.auth.exception.AuthExceptionType;
import pretzel.dreamketcherbe.domain.auth.google.dto.GoogleUserInfo;
import pretzel.dreamketcherbe.domain.auth.repository.TokenProvider;
import pretzel.dreamketcherbe.domain.auth.repository.TokenRepository;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenprovider;
    private final TokenRepository tokenRepository;

    @Transactional
    public TokenResponse loginOrRegister(GoogleUserInfo googleUserInfo) {
        Member member = getOrCreateMember(googleUserInfo);
        return createToken(member.getId());
    }

    private Token getAndDeleteToken(String tokenId) {
        Token token = tokenRepository.findByTokenId(tokenId)
            .orElseThrow(() -> new AuthException(AuthExceptionType.INVALID_TOKEN));
        tokenRepository.deleteByTokenId(tokenId);
        return token;
    }

    private Member getOrCreateMember(GoogleUserInfo googleUserInfo) {
        return memberRepository.findBySocialId(googleUserInfo.socialId())
            .orElseGet(() -> memberRepository.save(googleUserInfo.toMember()));
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
        Token token = getAndDeleteToken(tokenId);
        return createToken(token.getMemberId());
    }

    public void logout(Long memberId, String tokenId) {
        Token token = getTokenById(tokenId);
        validateTokenOwnership(token, memberId);
        tokenRepository.deleteByTokenId(tokenId);
    }

    public Token getTokenById(String tokenId) {
        return tokenRepository.findByTokenId(tokenId)
            .orElseThrow(() -> new AuthException(AuthExceptionType.INVALID_TOKEN));
    }

    public void validateTokenOwnership(Token token, Long memberId) {
        if (token.isMatchedMemberId(memberId)) {
            return;
        }
        throw new AuthException(AuthExceptionType.DISMATCHED_AUTHORIZATION);
    }
}
