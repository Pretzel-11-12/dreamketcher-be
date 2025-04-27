package pretzel.dreamketcherbe.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.report.entity.ReportReason;

public interface ReportReasonRepository extends JpaRepository<ReportReason, Long> {

}
