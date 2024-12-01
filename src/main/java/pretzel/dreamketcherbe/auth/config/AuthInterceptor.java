package pretzel.dreamketcherbe.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pretzel.dreamketcherbe.auth.exception.AuthException;
import pretzel.dreamketcherbe.auth.exception.AuthExceptionType;
import pretzel.dreamketcherbe.auth.jwt.TokenExtractor;
import pretzel.dreamketcherbe.auth.jwt.TokenProvider;
import pretzel.dreamketcherbe.auth.service.internal.AuthContext;
import pretzel.dreamketcherbe.auth.utils.AuthHeaderExtractor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenExtractor tokenExtractor;
    private final AuthContext authContext;

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) throws Exception {
        log.info("AuthInterceptor preHandle: {}", request.getRequestURI());

        String token = AuthHeaderExtractor.extract(request)
            .orElseThrow(() -> new AuthException(AuthExceptionType.UNAUTHORIZED));
        Long memberId = tokenExtractor.extract(token);
        log.info("memberId: {}", memberId);
        authContext.setMemberId(memberId);
        return true;
    }
}
