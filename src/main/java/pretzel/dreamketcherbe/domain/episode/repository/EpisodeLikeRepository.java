package pretzel.dreamketcherbe.domain.episode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.episode.entity.EpisodeLike;

public interface EpisodeLikeRepository extends JpaRepository<EpisodeLike, Long> {

  long countByEpisodeId(Long episodeId);
}