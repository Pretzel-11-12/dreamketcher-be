package pretzel.dreamketcherbe.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pretzel.dreamketcherbe.auth.dto.RenewAccessTokenRequest;
import pretzel.dreamketcherbe.auth.dto.TokenResponse;
import pretzel.dreamketcherbe.auth.service.AuthFacadeService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacadeService authWorkflowService;

    @GetMapping("/{socialType}/callback")
    public ResponseEntity<TokenResponse> loginOrRegister(
        @PathVariable("socialType") String socialType,
        @RequestParam("code") String code
    ) {
        log.info("socialType: {}, code: {}", socialType, code);
        return ResponseEntity.ok(authWorkflowService.loginOrRegister(code));
    }

    @GetMapping("/renew")
    public ResponseEntity<TokenResponse> renewAccessToken(
        @RequestHeader @Valid RenewAccessTokenRequest renewAccessTokenRequest
        ) {
        log.info("renewAccessTokenRequest: {}", renewAccessTokenRequest);
        return ResponseEntity.ok(authWorkflowService.renewAccessToken(renewAccessTokenRequest));
    }
}