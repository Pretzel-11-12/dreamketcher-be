package pretzel.dreamketcherbe.domain.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeException;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeExceptionType;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeLikeNotification;
import pretzel.dreamketcherbe.domain.notification.event.EpisodeLikeNotificationEvent;
import pretzel.dreamketcherbe.domain.notification.repository.EpisodeLikeNotificationRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final EpisodeRepository episodeRepository;
    private final EpisodeLikeNotificationRepository episodeLikeNotificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void likeNotification(Long episodeId, Long memberId) {
        Episode episode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        boolean isNotify = episode.incrementLikeCount();
        episodeRepository.save(episode);

        if (isNotify && !hasNotification(episodeId, episode.getLikeCount())) {

            saveLikeNotification(episode);

            EpisodeLikeNotificationEvent event = new EpisodeLikeNotificationEvent(
                episode.getId(),
                episode.getMember().getId(),
                episode.getWebtoon().getTitle(),
                episode.getNo(),
                episode.getTitle(),
                episode.getLikeCount(),
                LocalDateTime.now()
            );
            eventPublisher.publishEvent(event);
        }
    }

    private boolean hasNotification(Long episodeId, int likeCount) {
        return episodeLikeNotificationRepository.existsByEpisodeIdAndEpisodeLikeCount(episodeId,
            likeCount);
    }

    private void saveLikeNotification(Episode episode) {
        EpisodeLikeNotification notification = EpisodeLikeNotification.builder()
            .authorId(episode.getMember().getId())
            .episode(episode)
            .webtoonId(episode.getWebtoon().getId())
            .likeCount(episode.getLikeCount())
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();

        episodeLikeNotificationRepository.save(notification);
    }

    /**
     * 전체 알림 조회
     */
    @Transactional(readOnly = true)
    public List<EpisodeLikeNotification> getAllNotifications(Long memberId) {
        return episodeLikeNotificationRepository.findAllNotificationsByAuthorId(memberId,
            LocalDateTime.now());
    }

    /**
     * 읽지 않은 알림 조회
     */
    @Transactional(readOnly = true)
    public List<EpisodeLikeNotification> getUnreadNotifications(Long memberId) {
        return episodeLikeNotificationRepository.findUnreadNotificationsByAuthorId(memberId,
            LocalDateTime.now());
    }

    /**
     * 읽지 않은 알림 갯수 조회
     */
    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount(Long memberId) {
        return episodeLikeNotificationRepository.countUnreadNotificationsByAuthorId(memberId,
            LocalDateTime.now());
    }

    /**
     * 알림 읽음 처리
     */
    public void markAsReadNotification(Long notificationId) {
        EpisodeLikeNotification notification = episodeLikeNotificationRepository.findById(
                notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));

        notification.markAsRead();
        episodeLikeNotificationRepository.save(notification);
    }

    /**
     * 알림 만료 처리
     */
    public void cleanupExpiredNotifications() {
        List<EpisodeLikeNotification> expiredNotifications = episodeLikeNotificationRepository.
            findExpiredNotifications(LocalDateTime.now());

        episodeLikeNotificationRepository.deleteAll(expiredNotifications);
        log.info("만료 알림 {}개 삭제 완료", expiredNotifications.size());
    }
}
