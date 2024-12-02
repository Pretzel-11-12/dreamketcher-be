package pretzel.dreamketcherbe.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pretzel.dreamketcherbe.auth.dto.LogoutRequest;
import pretzel.dreamketcherbe.auth.dto.RenewAccessTokenRequest;
import pretzel.dreamketcherbe.auth.dto.TokenResponse;
import pretzel.dreamketcherbe.auth.service.AuthFacadeService;
import pretzel.dreamketcherbe.common.annotation.Auth;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacadeService authFacadeService;

    @GetMapping("/{socialType}/callback")
    public ResponseEntity<TokenResponse> loginOrRegister(
        @PathVariable("socialType") String socialType,
        @RequestParam("code") String code
    ) {
        log.info("socialType: {}, code: {}", socialType, code);
        return ResponseEntity.ok(authFacadeService.loginOrRegister(code));
    }

    @GetMapping("/renew")
    public ResponseEntity<TokenResponse> renewAccessToken(
        @RequestHeader @Valid RenewAccessTokenRequest renewAccessTokenRequest
        ) {
        log.info("renewAccessTokenRequest: {}", renewAccessTokenRequest);
        return ResponseEntity.ok(authFacadeService.renewAccessToken(renewAccessTokenRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @Auth Long memberId,
        @RequestBody @Valid LogoutRequest logoutRequest
    ) {
        log.info("memberId: {}, logoutRequest: {}", memberId, logoutRequest);
        authFacadeService.logout(memberId, logoutRequest);
        return ResponseEntity.ok().build();
    }
}