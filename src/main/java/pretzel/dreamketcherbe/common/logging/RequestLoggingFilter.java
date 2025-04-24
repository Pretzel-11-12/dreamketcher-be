package pretzel.dreamketcherbe.common.logging;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final HikariDataSource hikariDataSource;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        ContentCachingRequestWrapper cachedRequest =
            new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper cachedResponse =
            new ContentCachingResponseWrapper(response);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        filterChain.doFilter(cachedRequest, cachedResponse);

        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        logging(cachedRequest, cachedResponse, totalTimeMillis);

        cachedResponse.copyBodyToResponse();
    }

    private void logging(
        ContentCachingRequestWrapper request,
        ContentCachingResponseWrapper response,
        long totalTimeMillis
    ) {
        StringBuilder logMessage = new StringBuilder();

        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        String status = HttpStatus.valueOf(response.getStatus()).toString();
        logMessage.append("|\n| [REQUEST] (")
            .append(method).append(") ")
            .append(requestURI)
            .append("\n| >> STATUS_CODE: ")
            .append(status);

        getFormattedQueryString(queryString).ifPresent(v ->
            logMessage.append(v).append(queryString)
        );

        getRequestBody(request).ifPresent(v ->
            logMessage.append("\n| >> REQUEST_BODY: ").append(v)
        );

        getResponseBody(response).ifPresent(v ->
            logMessage.append("\n| >> RESPONSE_BODY: ").append(v)
        );

        String remoteIp = request.getRemoteAddr();
        String xff = Optional.ofNullable(request.getHeader("X-Forwarded-For")).orElse("-");
        String ua = Optional.ofNullable(request.getHeader("User-Agent")).orElse("Unknown");

        logMessage
            .append("\n| >> REMOTE_ADDR: ").append(remoteIp)
            .append("\n| >> X-Forwarded-For: ").append(xff)
            .append("\n| >> USER_AGENT: ").append(ua);

        ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();
        int threadCount = tmxb.getThreadCount();
        logMessage.append("\n| >> THREAD_COUNT: ").append(threadCount);

        HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();
        int totalConnections = poolMXBean.getTotalConnections();
        int activeConnections = poolMXBean.getActiveConnections();
        int idleConnections = poolMXBean.getIdleConnections();

        logMessage.append("\n| >> DB_CONNECTION: ")
            .append("totalConnections=").append(totalConnections)
            .append(", activeConnections=").append(activeConnections)
            .append(", idleConnections=").append(idleConnections);

        logMessage.append("\n| >> TOTAL_LATENCY_TIME: ").append(totalTimeMillis)
            .append("ms");

        if (response.getStatus() < 500) {
            log.info(logMessage.toString());
        } else {
            log.error(logMessage.toString());
        }
    }

    private Optional<String> getFormattedQueryString(String queryString) {
        if (StringUtils.hasText(queryString)) {
            return Optional.of("?");
        }
        return Optional.empty();
    }

    private Optional<String> getRequestBody(ContentCachingRequestWrapper request) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
            request.getContentAsByteArray());
        String requestBody = StreamUtil.toString(inputStream);
        if (StringUtils.hasText(requestBody)) {
            return Optional.of(requestBody);
        }
        return Optional.empty();
    }

    private Optional<String> getResponseBody(ContentCachingResponseWrapper response) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
            response.getContentAsByteArray());
        String responseBody = StreamUtil.toString(inputStream);
        if (StringUtils.hasText(responseBody)) {
            return Optional.of(responseBody);
        }
        return Optional.empty();
    }

    public static class StreamUtil {

        public static String toString(ByteArrayInputStream inputStream) {
            try {
                return org.springframework.util.StreamUtils.copyToString(inputStream,
                    java.nio.charset.StandardCharsets.UTF_8);
            } catch (IOException ex) {
                return "";
            }
        }
    }
}
