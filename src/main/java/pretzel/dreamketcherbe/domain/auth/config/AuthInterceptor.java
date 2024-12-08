package pretzel.dreamketcherbe.domain.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pretzel.dreamketcherbe.domain.auth.exception.AuthException;
import pretzel.dreamketcherbe.domain.auth.exception.AuthExceptionType;
import pretzel.dreamketcherbe.domain.auth.repository.TokenExtractor;
import pretzel.dreamketcherbe.domain.auth.service.internal.AuthContext;
import pretzel.dreamketcherbe.domain.auth.utils.AuthHeaderExtractor;

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

        String token = AuthHeaderExtractor.extract(request)
            .orElseThrow(() -> new AuthException(AuthExceptionType.UNAUTHORIZED));
        Long memberId = tokenExtractor.extractAccessToken(token);

        authContext.setMemberId(memberId);
        return true;
    }
}