package pretzel.dreamketcherbe.domain.webtoon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.webtoon.entity.SerializationPeriod;

import java.util.Optional;

public interface SerializationPeriodRepository extends JpaRepository<SerializationPeriod, Long> {

    Optional<SerializationPeriod> findByWebtoonId(Long webtoonId);
}
