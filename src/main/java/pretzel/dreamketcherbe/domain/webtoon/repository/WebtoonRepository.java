package pretzel.dreamketcherbe.domain.webtoon.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

import java.time.LocalDateTime;
import java.util.List;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {

    Page<Webtoon> findAllByStatus(String status, Pageable pageable);

    Page<Webtoon> findAllByStatusAndCreatedAtAfter(String status, LocalDateTime createdAt, Pageable pageable);

    @Query("SELECT w FROM Webtoon w, ManagementWebtoon m WHERE (w.title LIKE %:title% OR REPLACE(w.title, ' ', '') LIKE %:title%) AND m.approval = 'APPROVAL'")
    List<Webtoon> findByTitleContaining(@Param("title") String title);

    // TODO: 추후 멤버 상태 관리 추가 시 수정 필요
    @Query("SELECT w FROM Webtoon w JOIN w.member m WHERE m.nickname LIKE %:nickname% OR REPLACE(m.nickname, ' ', '') LIKE %:nickname%")
    List<Webtoon> findByMemberNickname(@Param("nickname") String nickname);

    Page<Webtoon> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = """
        SELECT 
            w.title,
            w.thumbnail,
            (SELECT GROUP_CONCAT(DISTINCT g.name SEPARATOR ',') 
             FROM webtoon_genres wg 
             JOIN genres g ON wg.genre_id = g.id 
             WHERE wg.webtoon_id = w.id) AS genres,
            w.episode_count,
            w.average_star,
            (SELECT COUNT(*) FROM episodes e WHERE e.webtoon_id = w.id) AS num_of_stars,
            (SELECT COUNT(*) FROM likes l WHERE l.webtoon_id = w.id) AS like_count,
            (SELECT SUM(e.view_count) FROM episodes e WHERE e.webtoon_id = w.id) AS view_count,
            (SELECT COUNT(*) FROM interested_webtoon iw WHERE iw.webtoon_id = w.id) AS interested_count
        FROM webtoons w
    """, nativeQuery = true)
    List<Object[]> findAllWithPopularityData();

}