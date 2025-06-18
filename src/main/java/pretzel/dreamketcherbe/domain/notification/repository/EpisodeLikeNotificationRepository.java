package pretzel.dreamketcherbe.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeLikeNotification;

public interface EpisodeLikeNotificationRepository extends
    JpaRepository<EpisodeLikeNotification, Long> {

    boolean existsByEpisodeIdAndEpisodeLikeCount(Long episodeId, int likeCount);

    // 전체 알림 조회
    @Query("SELECT n from EpisodeLikeNotification n WHERE n.authorId = :memberId AND n.expiredAt > :date ORDER BY n.createdAt DESC")
    List<EpisodeLikeNotification> findAllNotificationsByAuthorId(Long memberId,
        LocalDateTime date);

    // 읽지 않은 알림 조회
    @Query("SELECT n FROM EpisodeLikeNotification n WHERE n.authorId = :memberId and n.isRead = false AND n.expiredAt > :date ORDER BY n.createdAt DESC")
    List<EpisodeLikeNotification> findUnreadNotificationsByAuthorId(Long memberId,
        LocalDateTime date);

    // 읽지 않은 알림 갯수
    @Query("SELECT count (n) FROM EpisodeLikeNotification n WHERE n.authorId = :memberId AND n.isRead = false AND n.expiredAt >:date")
    Long countUnreadNotificationsByAuthorId(Long memberId, LocalDateTime date);

    // 만료 알림 조회
    @Query("SELECT n FROM EpisodeLikeNotification n WHERE n.expiredAt <= :date ORDER BY n.createdAt DESC")
    List<EpisodeLikeNotification> findExpiredNotifications(LocalDateTime date);
}
