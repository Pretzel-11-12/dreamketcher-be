package pretzel.dreamketcherbe.common.cookie;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieHandler {

  private static final Long DELETE_COOKIE_MAX_AGE = 0L;
  private static final String DELETE_COOKIE_VALUE = "";

  private final CookieProperties cookieProperties;

  public ResponseCookie createCookie(String cookieKey, String cookieValue) {
    return createCookieWithMaxAge(cookieKey, cookieValue, cookieProperties.maxAge());
  }

  public ResponseCookie deleteCookie(String cookieKey) {
    return createCookieWithMaxAge(cookieKey, DELETE_COOKIE_VALUE, DELETE_COOKIE_MAX_AGE);
  }

  private ResponseCookie createCookieWithMaxAge(String cookieKey, String cookieValue, Long maxAge) {
    return ResponseCookie.from(cookieKey, cookieValue)
        .maxAge(maxAge)
        .path(cookieProperties.path())
        .sameSite(cookieProperties.sameSite())
        .secure(cookieProperties.secure())
        .httpOnly(cookieProperties.httpOnly())
        .build();
  }
}
