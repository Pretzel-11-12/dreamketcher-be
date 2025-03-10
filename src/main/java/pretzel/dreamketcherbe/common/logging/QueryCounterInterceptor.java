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

    private static final int WARN_QUERY_COUNT = 8;
    private static final String LOG_FORMAT = "|\n| QUERY_COUNT: {}";

    private final QueryCounter queryCounter;
    private final LatencyContext latencyContext;

    @Override
    public void afterCompletion(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        Exception exception
    ) throws Exception {
        int queryCount = queryCounter.getQueryCount();

        if (queryCount < WARN_QUERY_COUNT) {
            log.info(LOG_FORMAT, queryCount);
        } else {
            log.warn(LOG_FORMAT, queryCount);
        }
    }
}
