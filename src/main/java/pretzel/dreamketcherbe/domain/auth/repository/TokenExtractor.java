package pretzel.dreamketcherbe.domain.auth.repository;

public interface TokenExtractor {

  Long extractAccessToken(String token);

  String extractRefreshToken(String token);
}
