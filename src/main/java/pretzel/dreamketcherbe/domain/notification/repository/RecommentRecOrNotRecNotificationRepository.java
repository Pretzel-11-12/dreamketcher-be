package pretzel.dreamketcherbe.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.notification.entity.RecommentNotificationType;
import pretzel.dreamketcherbe.domain.notification.entity.RecommentRecOrNotRecNotification;

public interface RecommentRecOrNotRecNotificationRepository extends
    JpaRepository<RecommentRecOrNotRecNotification, Long> {

    /**
     * 대댓글 추천/비추천 알림 존재 여부 확인
     */
    boolean existsByRecommentIdAndType(Long recommentId, RecommentNotificationType type);

    /**
     * 회원의 모든 대댓글 추천/비추천 알림 조회 (만료되지 않은 것만)
     */
    @Query("SELECT n FROM RecommentRecOrNotRecNotification n " +
        "WHERE n.memberId = :memberId " +
        "AND n.expiredAt > :date " +
        "ORDER BY n.createdAt DESC")
    List<RecommentRecOrNotRecNotification> findAllByMemberId(
        @Param("memberId") Long memberId,
        @Param("date") LocalDateTime date
    );

    /**
     * 회원의 읽지 않은 대댓글 추천/비추천 알림 조회 (만료되지 않은 것만)
     */
    @Query("SELECT n FROM RecommentRecOrNotRecNotification n " +
        "WHERE n.memberId = :memberId " +
        "AND n.isRead = false " +
        "AND n.expiredAt > :date " +
        "ORDER BY n.createdAt DESC")
    List<RecommentRecOrNotRecNotification> findAllUnreadNotificationsByMemberId(
        @Param("memberId") Long memberId,
        @Param("date") LocalDateTime date
    );

    /**
     * 회원의 읽지 않은 대댓글 추천/비추천 알림 수 조회 (만료되지 않은 것만)
     */
    @Query("SELECT COUNT(n) FROM RecommentRecOrNotRecNotification n " +
        "WHERE n.memberId = :memberId " +
        "AND n.isRead = false " +
        "AND n.expiredAt > :date")
    Long countUnreadNotificationsByMemberId(
        @Param("memberId") Long memberId,
        @Param("date") LocalDateTime date
    );

    /**
     * 만료된 대댓글 추천/비추천 알림 조회
     */
    @Query("SELECT n FROM RecommentRecOrNotRecNotification n " +
        "WHERE n.expiredAt <= :date")
    List<RecommentRecOrNotRecNotification> findExpiredNotifications(
        @Param("date") LocalDateTime date
    );
}
