package pretzel.dreamketcherbe.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeReportNotification;

public interface EpisodeReportNotificationRepository extends
    JpaRepository<EpisodeReportNotification, Long> {

    // 모든 신고 알림 조회
    @Query("SELECT n FROM EpisodeReportNotification n WHERE n.authorId = :memberId AND n.expiredAt > :dateTime ORDER BY n.createdAt DESC")
    List<EpisodeReportNotification> findAllNotificationsByMemberId(Long memberId,
        LocalDateTime dateTime);

    // 읽지 않은 신고 알림 조회
    @Query("SELECT n FROM EpisodeReportNotification n WHERE n.authorId = :memberId AND n.isRead = false AND n.expiredAt > :dateTime ORDER BY n.createdAt DESC")
    List<EpisodeReportNotification> findUnreadNotificationsByMemberId(Long memberId,
        LocalDateTime dateTime);

    // 읽지 않은 알림 수 조회
    @Query("SELECT COUNT (n) FROM EpisodeReportNotification n WHERE n.authorId = :memberId AND n.isRead = false AND n.expiredAt > :dateTime")
    Long countUnreadNotificationsByMemberId(Long memberId, LocalDateTime dateTime);

    // 만료된 신고 알림 조회
    @Query("SELECT n FROM EpisodeReportNotification n WHERE n.expiredAt <= :dateTime")
    List<EpisodeReportNotification> findExpiredNotifications(LocalDateTime dateTime);
}
