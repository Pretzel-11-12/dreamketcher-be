package pretzel.dreamketcherbe.domain.auth.controller;

import static org.springframework.http.HttpHeaders.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pretzel.dreamketcherbe.domain.auth.dto.TokenResponse;
import pretzel.dreamketcherbe.domain.auth.service.AuthFacadeService;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.common.cookie.CookieHandler;

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