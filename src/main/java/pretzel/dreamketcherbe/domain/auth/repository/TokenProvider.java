package pretzel.dreamketcherbe.domain.auth.repository;

public interface TokenProvider {

  String generatedAccessToken(Long memberId);

  String generatedRefreshToken(String tokenId);
}
