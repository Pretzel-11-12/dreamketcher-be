package pretzel.dreamketcherbe.auth.repository;

public interface TokenProvider {

    String generatedAccessToken(Long memberId);

    String generatedRefreshToken(Long memberId);
}
