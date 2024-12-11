package pretzel.dreamketcherbe.domain.episode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    @Modifying
    @Query("UPDATE Episode e SET e.viewCount = e.viewCount + 1 WHERE e.id = :episodeId")
    int increaseViewCount(Long episodeId);

}
