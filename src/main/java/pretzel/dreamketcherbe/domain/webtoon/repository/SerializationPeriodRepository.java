package pretzel.dreamketcherbe.domain.webtoon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.webtoon.entity.SerializationPeriod;

public interface SerializationPeriodRepository extends JpaRepository<SerializationPeriod, Long> {

    SerializationPeriod findByWebtoonId(Long webtoonId);
}
