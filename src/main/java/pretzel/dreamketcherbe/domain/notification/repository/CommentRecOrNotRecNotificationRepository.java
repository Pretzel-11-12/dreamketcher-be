package pretzel.dreamketcherbe.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.notification.entity.CommentNotificationType;
import pretzel.dreamketcherbe.domain.notification.entity.CommentRecOrNotRecNotification;

public interface CommentRecOrNotRecNotificationRepository extends
    JpaRepository<CommentRecOrNotRecNotification, Long> {

    // 추천/비추천
    @Query("SELECT COUNT(c) > 0 FROM CommentRecOrNotRecNotification c WHERE c.comment.id = :commentId AND c.type = :type AND c.isRead = false")
    Boolean existsByCommentIdAndType(Long commentId,
        CommentNotificationType type);

    // 모든 알림 조회
    @Query("SELECT c FROM CommentRecOrNotRecNotification c WHERE c.memberId = :memberId AND c.expiredAt > :date ORDER BY c.createdAt DESC")
    List<CommentRecOrNotRecNotification> findAllByMemberId(Long memberId, LocalDateTime date);

    // 읽지 않은 모든 알림 조회
    @Query("SELECT c FROM CommentRecOrNotRecNotification c WHERE c.memberId = :memberId AND c.isRead = false AND c.expiredAt > :date ORDER BY c.createdAt DESC")
    List<CommentRecOrNotRecNotification> findAllUnreadNotificationsByMemberId(Long memberId,
        LocalDateTime date);

    // 읽지 않은 알림 수
    @Query("SELECT COUNT(c) FROM CommentRecOrNotRecNotification c WHERE c.memberId = :memberId AND c.isRead = false AND c.expiredAt > :date")
    Long countUnreadNotificationsByMemberId(Long memberId, LocalDateTime date);

    // 만료 알림 조회
    @Query("SELECT c FROM CommentRecOrNotRecNotification c WHERE c.expiredAt < :date")
    List<CommentRecOrNotRecNotification> findExpiredNotifications(LocalDateTime date);
}
