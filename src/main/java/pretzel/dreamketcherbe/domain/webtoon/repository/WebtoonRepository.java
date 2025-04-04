package pretzel.dreamketcherbe.domain.webtoon.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.ranking.repository.RankingRepositoryCustom;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long>, WebtoonRepositoryCustom,
    RankingRepositoryCustom {

    @Query("SELECT DISTINCT w FROM Webtoon w " +
        "JOIN w.member m " +
        "WHERE (LOWER(w.title) LIKE %:keyword% OR LOWER(REPLACE(w.title, ' ', '')) LIKE %:keyword%) "
        +
        "OR (LOWER(m.nickname) LIKE %:keyword% OR LOWER(REPLACE(m.nickname, ' ', '')) LIKE %:keyword%)"
        +
        "AND w.isDeleted = false")
    Page<Webtoon> findByTitleOrMemberNickname(@Param("keyword") String keyword, Pageable pageable);

    Page<Webtoon> findAllByOrderByCreatedAtDesc(Pageable pageable);
}