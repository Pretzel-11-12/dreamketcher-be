package pretzel.dreamketcherbe.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.admin.entity.ManagementEpisode;

import java.util.Optional;

public interface ManagementEpisodeRepository extends JpaRepository<ManagementEpisode, Long> {
    Optional<ManagementEpisode> findByEpisodeId(Long episodeId);
}
