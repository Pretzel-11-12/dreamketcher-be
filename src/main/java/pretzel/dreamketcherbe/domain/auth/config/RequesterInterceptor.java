package pretzel.dreamketcherbe.domain.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pretzel.dreamketcherbe.common.annotation.Requester;
import pretzel.dreamketcherbe.domain.auth.dto.RequestInfo;
import pretzel.dreamketcherbe.domain.auth.repository.TokenExtractor;
import pretzel.dreamketcherbe.domain.auth.service.internal.RequesterContext;
import pretzel.dreamketcherbe.domain.auth.utils.AuthHeaderExtractor;

@Component
@RequiredArgsConstructor
public class RequesterInterceptor implements HandlerInterceptor {

    private final TokenExtractor tokenExtractor;
    private final RequesterContext requesterContext;

    /**
     * 컨트롤러 메서드에 @Requester 애노테이션이 있는 경우, 요청자의 정보를 추출하여 RequesterContext에 설정합니다.
     *
     * 인증 토큰이 있으면 회원 정보를, 없거나 유효하지 않으면 클라이언트 IP 기반의 게스트 정보를 설정합니다.
     * 항상 true를 반환하여 요청 처리를 계속 진행합니다.
     *
     * @param request  현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @param handler  처리할 핸들러 객체
     * @return 항상 true를 반환하여 요청 처리를 계속 진행함
     */
    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) throws Exception {
        if (!(handler instanceof HandlerMethod hm)) {
            return true;
        }

        boolean hasRequesterAnnotation = Arrays.stream(hm.getMethodParameters())
            .anyMatch(p -> p.hasParameterAnnotation(Requester.class));

        if (!hasRequesterAnnotation) {
            return true;
        }

        String ip = getClientIp(request);
        String token = AuthHeaderExtractor.extract(request)
            .orElse(null);

        if (token != null && !token.isEmpty()) {
            try {
                Long memberId = tokenExtractor.extractAccessToken(token);
                requesterContext.setRequester(RequestInfo.fromMember(memberId));
            } catch (Exception e) {
                requesterContext.setRequester(RequestInfo.fromGuest(ip));
            }
        } else {
            requesterContext.setRequester(RequestInfo.fromGuest(ip));
        }

        return true;
    }

    /**
     * HTTP 요청에서 클라이언트의 IP 주소를 추출합니다.
     *
     * 여러 프록시 및 네트워크 환경을 고려하여 다양한 HTTP 헤더에서 IP를 순차적으로 조회하며,
     * 유효한 값이 없을 경우 기본적으로 request의 remote address를 반환합니다.
     *
     * @param request 클라이언트의 IP를 추출할 HTTP 요청 객체
     * @return 추출된 클라이언트 IP 주소 문자열
     */
    private String getClientIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-RealIP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
