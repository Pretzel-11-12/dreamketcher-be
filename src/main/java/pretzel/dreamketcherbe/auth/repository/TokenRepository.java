package pretzel.dreamketcherbe.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.auth.entity.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenId(String tokenId);

    void deleteByTokenId(String tokenId);
}
