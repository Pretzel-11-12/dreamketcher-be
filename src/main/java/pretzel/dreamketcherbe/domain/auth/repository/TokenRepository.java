package pretzel.dreamketcherbe.domain.auth.repository;

import pretzel.dreamketcherbe.domain.auth.entity.Token;

import java.util.Optional;

public interface TokenRepository {
    Optional<Token> findByTokenId(String tokenId);

    void deleteByTokenId(String tokenId);

    void save(Token token);
}
