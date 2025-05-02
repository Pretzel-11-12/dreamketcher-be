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
