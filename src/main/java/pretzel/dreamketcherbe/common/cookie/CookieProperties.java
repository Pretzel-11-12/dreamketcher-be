package pretzel.dreamketcherbe.common.cookie;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cookie")
public record CookieProperties(
    Long maxAge,
    String path,
    String sameSite,
    boolean httpOnly,
    boolean secure
) {
}
