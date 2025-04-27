package pretzel.dreamketcherbe.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.report.entity.EpisodeReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

public interface EpisodeReportRepository extends JpaRepository<EpisodeReport, Long> {

    Page<EpisodeReport> findByStatus(ReportStatus status, Pageable pageable);
}
