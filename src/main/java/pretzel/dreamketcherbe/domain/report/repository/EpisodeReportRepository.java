package pretzel.dreamketcherbe.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.report.entity.EpisodeReport;

public interface EpisodeReportRepository extends JpaRepository<EpisodeReport, Long> {

}
