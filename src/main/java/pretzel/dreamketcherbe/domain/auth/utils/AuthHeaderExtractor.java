package pretzel.dreamketcherbe.domain.auth.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Optional;

public class AuthHeaderExtractor {

    private static final String BEARER = "Bearer ";

    public static Optional<String> extract(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER)) {
            return Optional.of(authHeader.substring(BEARER.length()).trim());
        }
        return Optional.empty();
    }
}
