package pretzel.dreamketcherbe.domain.episode.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    // 동일 날짜 미발행 웹툰 조회
    List<Episode> findByPublishedAtAndPublishedFalse(LocalDate publishedAt);

    @Query("SELECT e FROM Episode e WHERE e.id = :episodeId AND e.published = true")
    Optional<Episode> findByIsDeletedFalseAndPublishedTrue(@Param("episodeId") Long episodeId);

    // 에피소드 카운트
    @Query("SELECT COUNT(e) FROM Episode e WHERE e.webtoon.id = :webtoonId AND e.status = 'NORMAL' AND e.published = true")
    int CountByWebtoonId(@Param("webtoonId") Long webtoonId);

    @Modifying
    @Query("UPDATE Episode e SET e.viewCount = e.viewCount + 1 WHERE e.id = :episodeId AND e.published = true AND e.status = 'NORMAL'")
    void increaseViewCount(@Param("episodeId") Long episodeId);

    @Query("SELECT e FROM Episode e WHERE e.webtoon.id = :webtoonId AND e.status = 'NORMAL' ORDER BY e.publishedAt DESC")
    Page<Episode> findByWebtoonIdOrderByPublishedAtDesc(Long webtoonId, Pageable pageable);

    @Query("SELECT e FROM Episode e WHERE e.webtoon.id = :webtoonId AND e.status = 'NORMAL' ORDER BY e.publishedAt ASC")
    Page<Episode> findByWebtoonIdOrderByPublishedAtAsc(Long webtoonId, Pageable pageable);

    Page<Episode> findAllByWebtoonId(Long webtoonId, Pageable pageable);

    @Query("SELECT e FROM Episode e WHERE e.webtoon.id = :webtoonId AND e.no BETWEEN :startNo AND :endNo ORDER BY e.no ASC")
    List<Episode> findEpisodesAround(
        @Param("webtoonId") Long webtoonId,
        @Param("startNo") int startNo,
        @Param("endNo") int endNo
    );

    Long countByWebtoonId(Long webtoonId);

    @Query("SELECT e.id FROM Episode e WHERE e.webtoon.id = :webtoonId AND e.status = 'NORMAL' AND e.published = true")
    List<Long> findByWebtoonId(@Param("webtoonId") Long webtoonId);

    @Modifying
    @Query("UPDATE Episode e SET e.status = 'DELETED' WHERE e.webtoon.id =:webtoonId")
    void deleteByWebtoonId(@Param("webtoonId") Long webtoonId);

    @Modifying
    @Transactional
    @Query("UPDATE Episode e SET e.published = true WHERE e.id IN :episodeIds")
    void updatePublishedById(@Param("episodeIds") List<Long> episodeIds);
}