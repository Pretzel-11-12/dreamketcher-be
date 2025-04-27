package pretzel.dreamketcherbe.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.report.entity.CommentReport;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

}
