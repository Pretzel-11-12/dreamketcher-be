package pretzel.dreamketcherbe.domain.auth.config;

import static java.util.Arrays.stream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pretzel.dreamketcherbe.common.annotation.Auth;
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

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;

            boolean hasAuthAnnotation = stream(hm.getMethodParameters())
                .anyMatch(p -> p.hasParameterAnnotation(Auth.class));

            if (!hasAuthAnnotation) {
                return true;
            }
        }

        String token = AuthHeaderExtractor.extract(request)
            .orElseThrow(() -> new AuthException(AuthExceptionType.UNAUTHORIZED));
        Long memberId = tokenExtractor.extractAccessToken(token);

        authContext.setMemberId(memberId);
        return true;
    }
}