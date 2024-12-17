package pretzel.dreamketcherbe.domain.episode.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.episode.entity.EpisodeStar;

public interface EpisodeStarRepository extends JpaRepository<EpisodeStar, Long> {

    List<EpisodeStar> findByEpisodeId(Long episodeId);
}
