package pretzel.dreamketcherbe.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.notification.entity.CommentReportNotification;

public interface CommentReportNotificationRepository extends
    JpaRepository<CommentReportNotification, Long> {

    // 모든 댓글 신고 알림 조회
    @Query("SELECT n FROM CommentReportNotification n WHERE (n.memberId = :memberId OR n.reporterId = :memberId) AND n.expiredAt >: dateTime ORDER BY n.createdAt DESC")
    List<CommentReportNotification> findAllCommentReportNotificationsByMemberId(Long memberId,
        LocalDateTime dateTime);

    // 읽지 않은 댓글 신고 알림 조회
    @Query("SELECT n FROM CommentReportNotification n WHERE (n.memberId = :memberId OR n.reporterId = :memberId) AND n.isRead = false AND n.expiredAt >: dateTime ORDER BY n.createdAt DESC")
    List<CommentReportNotification> findUnreadCommentReportNotificationsByMemberId(Long memberId,
        LocalDateTime dateTime);

    // 읽지 않은 댓글 신고 알림 수 조회
    @Query("SELECT COUNT (n) FROM CommentReportNotification n WHERE n.memberId = :memberId OR n.reporterId = :memberId AND n.isRead = false AND n.expiredAt >: dateTime")
    Long countUnreadCommentReportNotificationsByMemberId(Long memberId, LocalDateTime dateTime);

    // 만료된 댓글 신고 알림 조회
    @Query("SELECT n FROM CommentReportNotification n WHERE n.expiredAt <= :dateTime")
    List<CommentReportNotification> findExpiredCommentReportNotifications(LocalDateTime dateTime);
}
