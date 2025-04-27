package pretzel.dreamketcherbe.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.report.entity.CommentReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    Page<CommentReport> findByStatus(ReportStatus status, Pageable pageable);
}
