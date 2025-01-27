package pretzel.dreamketcherbe.domain.episode.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    // 동일 날짜 미발행 웹툰 조회
    List<Episode> findByPublishedAtAndPublishedFalse(LocalDate publishedAt);
    @Modifying
    @Query("UPDATE Episode e SET e.viewCount = e.viewCount + 1 WHERE e.id = :episodeId")
    void increaseViewCount(Long episodeId);

    Page<Episode> findByWebtoonIdOrderByPublishedAtDesc(Long webtoonId, Pageable pageable);

    Page<Episode> findByWebtoonIdOrderByPublishedAtAsc(Long webtoonId, Pageable pageable);

    Page<Episode> findAllByWebtoonId(Long webtoonId, Pageable pageable);

    Long countByWebtoonId(Long webtoonId);
}