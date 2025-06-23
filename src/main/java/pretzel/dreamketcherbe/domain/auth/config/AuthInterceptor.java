package pretzel.dreamketcherbe.domain.auth.config;

import static java.util.Arrays.stream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.common.annotation.Admin;
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

    /**
     * 컨트롤러 메서드의 파라미터에 {@code @Auth} 또는 {@code @Admin} 어노테이션이 있을 경우 인증 토큰을 검사하고, 인증 정보를 컨텍스트에 저장합니다.
     *
     * 인증 어노테이션이 없는 경우 인증 없이 요청을 허용합니다.
     * 인증 토큰이 없으면 {@code AuthException}을 발생시킵니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler  현재 처리 중인 핸들러 객체
     * @return 인증이 성공하거나 인증이 필요 없는 경우 {@code true}
     * @throws AuthException 인증 토큰이 없을 때 발생
     */
    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;

            boolean hasAuthAnnotation = stream(hm.getMethodParameters())
                .anyMatch(p -> p.hasParameterAnnotation(Auth.class) || p.hasParameterAnnotation(Admin.class));

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