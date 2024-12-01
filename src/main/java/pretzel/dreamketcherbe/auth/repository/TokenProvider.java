package pretzel.dreamketcherbe.auth.repository;

public interface TokenProvider {

    String generated(Long memberId);
}
