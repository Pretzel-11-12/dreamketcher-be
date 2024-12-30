package pretzel.dreamketcherbe.domain.auth.controller;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.common.cookie.CookieHandler;
import pretzel.dreamketcherbe.domain.auth.dto.TokenResponse;
import pretzel.dreamketcherbe.domain.auth.service.AuthFacadeService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String COOKIE_REFRESH_TOKEN = "refresh_token";

    private final AuthFacadeService authFacadeService;
    private final CookieHandler cookieHandler;

    @GetMapping("/{socialType}/callback")
    public ResponseEntity<TokenResponse> loginOrRegister(
        @PathVariable("socialType") String socialType,
        @RequestParam("code") String code
    ) {
        TokenResponse tokenResponse = authFacadeService.loginOrRegister(code);
        ResponseCookie cookie = cookieHandler.createCookie(COOKIE_REFRESH_TOKEN,
            tokenResponse.refreshToken());
        return ResponseEntity.status(HttpStatus.OK)
            .header(SET_COOKIE, cookie.toString())
            .body(tokenResponse);
    }

    @GetMapping("/renew")
    public ResponseEntity<TokenResponse> renewAccessToken(
        @CookieValue(COOKIE_REFRESH_TOKEN) String refreshToken
    ) {
        TokenResponse tokenResponse = authFacadeService.renewAccessToken(refreshToken);
        ResponseCookie cookie = cookieHandler.createCookie(COOKIE_REFRESH_TOKEN,
            tokenResponse.refreshToken());
        return ResponseEntity.status(HttpStatus.OK)
            .header(SET_COOKIE, cookie.toString())
            .body(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @Auth Long memberId,
        @CookieValue(COOKIE_REFRESH_TOKEN) String refreshToken
    ) {
        authFacadeService.logout(memberId, refreshToken);
        ResponseCookie cookie = cookieHandler.deleteCookie(COOKIE_REFRESH_TOKEN);
        return ResponseEntity.status(HttpStatus.OK)
            .header(SET_COOKIE, cookie.toString())
            .build();
    }
}