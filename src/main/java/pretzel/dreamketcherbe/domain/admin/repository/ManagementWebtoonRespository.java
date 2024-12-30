package pretzel.dreamketcherbe.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.admin.entity.ManagementWebtoon;

import java.util.Optional;

public interface ManagementWebtoonRespository extends JpaRepository<ManagementWebtoon, Long> {

    Optional<ManagementWebtoon> findByWebtoonId(Long webtoonId);
}
