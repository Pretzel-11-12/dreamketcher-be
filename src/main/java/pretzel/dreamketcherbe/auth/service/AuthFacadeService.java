package pretzel.dreamketcherbe.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.auth.dto.RenewAccessTokenRequest;
import pretzel.dreamketcherbe.auth.dto.TokenResponse;
import pretzel.dreamketcherbe.auth.google.GoogleOAuthClient;
import pretzel.dreamketcherbe.auth.google.dto.GoogleUserInfo;
import pretzel.dreamketcherbe.auth.repository.TokenExtractor;

@Service
@RequiredArgsConstructor
public class AuthFacadeService {

    private final AuthService authService;
    private final TokenExtractor TokenExtractor;
    private final GoogleOAuthClient googleOAuthClient;

    public TokenResponse loginOrRegister(String code) {
        GoogleUserInfo googleUserInfo = googleOAuthClient.getOAuthInfo(code);
        return authService.loginOrRegister(googleUserInfo);
    }

    public TokenResponse renewAccessToken(RenewAccessTokenRequest request) {
        String refreshToken = request.refreshToken();
        String tokenId = TokenExtractor.extractRefreshToken(refreshToken);
        return authService.refreshTokenById(tokenId);
    }
}
