package pretzel.dreamketcherbe.auth.jwt;

public interface TokenExtractor {

    Long extract(String token);
}
