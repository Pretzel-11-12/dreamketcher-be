package pretzel.dreamketcherbe.domain.webtoon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

import java.time.LocalDateTime;
import java.util.List;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {
    List<Webtoon> findAllByStatus(String status);

    List<Webtoon> findAllByStatusAndCreatedAtAfter(String status, LocalDateTime createdAt);

    Page<Webtoon> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
