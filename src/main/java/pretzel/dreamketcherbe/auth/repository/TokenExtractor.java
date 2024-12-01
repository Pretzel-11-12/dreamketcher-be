package pretzel.dreamketcherbe.auth.repository;

public interface TokenExtractor {

    Long extract(String token);
}
