package pretzel.dreamketcherbe.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.auth.dto.TokenResponse;
import pretzel.dreamketcherbe.domain.auth.google.GoogleOAuthClient;
import pretzel.dreamketcherbe.domain.auth.google.GoogleProperties;
import pretzel.dreamketcherbe.domain.auth.google.dto.GoogleUserInfo;
import pretzel.dreamketcherbe.domain.auth.repository.TokenExtractor;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacadeService {

    private final AuthService authService;
    private final TokenExtractor tokenExtractor;
    private final GoogleOAuthClient googleOAuthClient;
    private final GoogleProperties googleProperties;

    public TokenResponse loginOrRegister(String code, String baseUrl) {
        boolean isLocal = authService.isLocalBaseUrl(baseUrl);
        log.info("=======================isLocal: {}=======================", isLocal);
        log.info("=======================baseUrl: {}=======================", baseUrl);
        String redirectUri =
            isLocal ? googleProperties.localRedirectUri() : googleProperties.devRedirectUri();
        log.info("=======================redirectUri: {}=======================", redirectUri);
        GoogleUserInfo googleUserInfo = googleOAuthClient.getOAuthInfo(code, redirectUri);
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
