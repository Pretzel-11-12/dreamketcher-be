package pretzel.dreamketcherbe.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pretzel.dreamketcherbe.auth.dto.TokenResponse;
import pretzel.dreamketcherbe.auth.service.AuthService;
import pretzel.dreamketcherbe.auth.service.AuthWorkflowService;
import pretzel.dreamketcherbe.common.cookie.CookieHandler;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private AuthService authService;

    @GetMapping("/{socialType}/callback")
    public void loginOrRegister(
        @PathVariable("socialType") String socialType,
        @RequestParam("code") String code
    ) {
        log.info("socialType: {}, code: {}", socialType, code);
        authService.loginOrRegister(socialType, code);
    }
}
