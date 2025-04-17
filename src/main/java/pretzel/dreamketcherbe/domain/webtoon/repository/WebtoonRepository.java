package pretzel.dreamketcherbe.domain.webtoon.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.ranking.repository.RankingRepositoryCustom;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long>, WebtoonRepositoryCustom,
    RankingRepositoryCustom {

    @Query("""
            SELECT DISTINCT w
            FROM Webtoon w
            JOIN w.member m
            WHERE (LOWER(w.title) LIKE %:keyword%
            OR LOWER(REPLACE(w.title, ' ', '')) LIKE %:keyword%)
            AND w.isDeleted = false
        """)
    Page<Webtoon> findByTitle(@Param("keyword") String keyword, Pageable pageable);

    Page<Webtoon> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 특정 작가의 첫 작품 (등록일 오름차순)
    Optional<Webtoon> findFirstByMemberAndIsDeletedFalseOrderByCreatedAtAsc(Member member);

    // 특정 작가의 작품 수
    long countByMemberAndIsDeletedFalse(Member member);
}