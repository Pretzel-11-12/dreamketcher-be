package pretzel.dreamketcherbe.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.auth.dto.TokenResponse;
import pretzel.dreamketcherbe.domain.auth.google.GoogleOAuthClient;
import pretzel.dreamketcherbe.domain.auth.google.dto.GoogleUserInfo;
import pretzel.dreamketcherbe.domain.auth.repository.TokenExtractor;

@Service
@RequiredArgsConstructor
public class AuthFacadeService {

    private final AuthService authService;
    private final TokenExtractor tokenExtractor;
    private final GoogleOAuthClient googleOAuthClient;

    public TokenResponse loginOrRegister(String code) {
        GoogleUserInfo googleUserInfo = googleOAuthClient.getOAuthInfo(code);
        return authService.loginOrRegister(googleUserInfo);
    }

    public TokenResponse renewAccessToken(String refreshToken) {
        String tokenId = tokenExtractor.extractRefreshToken(refreshToken);
        return authService.refreshTokenById(tokenId);
    }

    public void logout(Long memberId, String refreshToken) {
        String tokenId = tokenExtractor.extractRefreshToken(refreshToken);
        authService.logout(memberId, tokenId);
    }
}
