package pretzel.dreamketcherbe.domain.notification.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
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
            .episode(episode)
            .episodeLikeCount(episode.getLikeCount())
            .build();

        episodeLikeNotificationRepository.save(notification);
    }
}
