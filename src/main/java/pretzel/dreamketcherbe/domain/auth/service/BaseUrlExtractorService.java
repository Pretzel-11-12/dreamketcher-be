package pretzel.dreamketcherbe.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BaseUrlExtractorService {

    // TODO: Prod 환경 배포 시 삭제
    public String extractBaseUrl(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String baseUrl = "Unknown";

        if (referer != null && !referer.isEmpty()) {
            try {
                URI refererUri = new URI(referer);
                String scheme = refererUri.getScheme();
                String host = refererUri.getHost();
                int port = refererUri.getPort();

                if (scheme == null || host == null) {
                    log.warn("Scheme or host is null in Referer URI: {}", referer);
                    return baseUrl;
                }

                if ((scheme.equals("http") && port == 80) ||
                    (scheme.equals("https") && port == 443) ||
                    port == -1) {
                    baseUrl = String.format("%s://%s", scheme, host);
                } else {
                    baseUrl = String.format("%s://%s:%d", scheme, host, port);
                }
            } catch (URISyntaxException e) {
                log.error("Invalid Referer URI: {}", referer, e);
            }
        } else {
            log.warn("Referer header is missing or empty.");
        }

        return baseUrl;
    }
}