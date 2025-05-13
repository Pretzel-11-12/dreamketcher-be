package pretzel.dreamketcherbe.domain.report.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.report.entity.ReportReason;
import pretzel.dreamketcherbe.domain.report.entity.ReportReasonCode;

public interface ReportReasonRepository extends JpaRepository<ReportReason, Long> {

    Optional<ReportReason> findByCode(ReportReasonCode reasonCode);
}
