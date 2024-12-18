package pretzel.dreamketcherbe.domain.auth.repository;

import java.util.Optional;
import pretzel.dreamketcherbe.domain.auth.entity.Token;

public interface TokenRepository {

    Optional<Token> findByTokenId(String tokenId);

    void deleteByTokenId(String tokenId);

    void save(Token token);
}
