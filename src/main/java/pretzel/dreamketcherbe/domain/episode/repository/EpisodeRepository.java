package pretzel.dreamketcherbe.domain.episode.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    @Modifying
    @Query("UPDATE Episode e SET e.viewCount = e.viewCount + 1 WHERE e.id = :episodeId")
    void increaseViewCount(Long episodeId);

    Page<Episode> findByWebtoonIdOrderByPublishedAtDesc(Long webtoonId, Pageable pageable);

    Page<Episode> findByWebtoonIdOrderByPublishedAtAsc(Long webtoonId, Pageable pageable);

    Page<Episode> findAllByWebtoonId(Long webtoonId, Pageable pageable);

    Long countByWebtoonId(Long webtoonId);

    @Query("SELECT e.id FROM Episode e WHERE e.webtoon.id = :webtoonId AND e.isDeleted = false ")
    List<Long> findByWebtoonId(@Param("webtoonId") Long webtoonId);

    @Modifying
    @Query("UPDATE Episode e SET e.isDeleted = true WHERE e.webtoon.id =:webtoonId")
    void deleteByWebtoonId(@Param("webtoonId") Long webtoonId);
}
