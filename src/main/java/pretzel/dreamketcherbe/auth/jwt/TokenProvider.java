package pretzel.dreamketcherbe.auth.jwt;

public interface TokenProvider {

    String generated(Long memberId);
}
