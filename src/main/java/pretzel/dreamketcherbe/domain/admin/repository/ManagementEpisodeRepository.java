package pretzel.dreamketcherbe.domain.admin.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.admin.entity.ManagementEpisode;

public interface ManagementEpisodeRepository extends JpaRepository<ManagementEpisode, Long> {

    Optional<ManagementEpisode> findByEpisodeId(Long episodeId);
}
