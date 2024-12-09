package pretzel.dreamketcherbe.domain.episode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

}
