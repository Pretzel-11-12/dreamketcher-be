package pretzel.dreamketcherbe.domain.webtoon.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {

    List<Webtoon> findAllByStatus(String status);

    List<Webtoon> findAllByStatusAndCreatedAtAfter(String status, LocalDateTime createdAt);

    @Query("SELECT w FROM Webtoon w WHERE (w.title LIKE %:title% OR REPLACE(w.title, ' ', '') LIKE %:title%) AND w.approval = 'approved'")
    List<Webtoon> findByTitleContaining(@Param("title") String title);

    // TODO: 추후 멤버 상태 관리 추가 시 수정 필요
    @Query("SELECT w FROM Webtoon w JOIN w.member m WHERE m.nickname LIKE %:nickname% OR REPLACE(m.nickname, ' ', '') LIKE %:nickname%")
    List<Webtoon> findByMemberNickname(@Param("nickname") String nickname);

    Page<Webtoon> findAllByOrderByCreatedAtDesc(Pageable pageable);
}