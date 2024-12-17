package pretzel.dreamketcherbe.domain.episode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.episode.entity.EpisodeStar;

import java.util.List;

public interface EpisodeStarRepository extends JpaRepository<EpisodeStar, Long> {

    List<EpisodeStar> findByEpisodeId(Long episodeId);
}
