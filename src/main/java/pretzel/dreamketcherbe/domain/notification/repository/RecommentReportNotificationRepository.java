package pretzel.dreamketcherbe.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.notification.entity.RecommentReportNotification;

public interface RecommentReportNotificationRepository extends
    JpaRepository<RecommentReportNotification, Long> {

    /**
     * 모든 답글 신고 알림 조회
     */
    @Query("SELECT n FROM RecommentReportNotification n " +
        "WHERE (n.memberId = :memberId OR n.reporterId = :memberId) " +
        "AND n.expiredAt > :now " +
        "ORDER BY n.id DESC")
    List<RecommentReportNotification> findAllRecommentReportNotificationsByMemberId(
        @Param("memberId") Long memberId,
        @Param("now") LocalDateTime now);

    /**
     * 읽지 않은 답글 신고 알림 조회
     */
    @Query("SELECT n FROM RecommentReportNotification n " +
        "WHERE (n.memberId = :memberId OR n.reporterId = :memberId) " +
        "AND n.isRead = false " +
        "AND n.expiredAt > :now " +
        "ORDER BY n.id DESC")
    List<RecommentReportNotification> findUnreadRecommentReportNotificationsByMemberId(
        @Param("memberId") Long memberId,
        @Param("now") LocalDateTime now);

    /**
     * 읽지 않은 답글 신고 알림 수 조회
     */
    @Query("SELECT COUNT(n) FROM RecommentReportNotification n " +
        "WHERE (n.memberId = :memberId OR n.reporterId = :memberId) " +
        "AND n.isRead = false " +
        "AND n.expiredAt > :now")
    Long countUnreadRecommentReportNotificationsByMemberId(
        @Param("memberId") Long memberId,
        @Param("now") LocalDateTime now);

    /**
     * 만료된 답글 신고 알림 조회
     */
    @Query("SELECT n FROM RecommentReportNotification n " +
        "WHERE n.expiredAt <= :now")
    List<RecommentReportNotification> findExpiredRecommentReportNotifications(
        @Param("now") LocalDateTime now);
}
