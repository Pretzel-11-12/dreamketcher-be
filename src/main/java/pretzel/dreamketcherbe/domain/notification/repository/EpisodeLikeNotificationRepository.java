package pretzel.dreamketcherbe.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeLikeNotification;

public interface EpisodeLikeNotificationRepository extends
    JpaRepository<EpisodeLikeNotification, Long> {

    boolean existsByEpisodeIdAndEpisodeLikeCount(Long episodeId, int episodeLikeCount);


}
