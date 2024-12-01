package pretzel.dreamketcherbe.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.auth.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
