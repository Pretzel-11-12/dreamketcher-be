package pretzel.dreamketcherbe.common.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryCounterInterceptor implements HandlerInterceptor {

    private static final String LOGGING_FORMAT = "|\n| QUERY_COUNT: {}";

    private final QueryCounter queryCounter;
    private final LatencyContext latencyContext;

    @Override
    public void afterCompletion(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        Exception exception
    ) throws Exception {
        log.info(
            LOGGING_FORMAT,
            request.getMethod(),
            request.getRequestURI(),
            response.getStatus(),
            queryCounter.getQueryCount(),
            latencyContext.getFullTime()
        );
    }
}
