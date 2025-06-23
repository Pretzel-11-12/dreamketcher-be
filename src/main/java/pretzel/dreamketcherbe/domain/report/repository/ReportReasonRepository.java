package pretzel.dreamketcherbe.domain.report.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.report.entity.ReportReason;
import pretzel.dreamketcherbe.domain.report.entity.ReportReasonCode;

public interface ReportReasonRepository extends JpaRepository<ReportReason, Long> {

    /**
 * 지정된 코드에 해당하는 신고 사유 엔티티를 조회합니다.
 *
 * @param reasonCode 조회할 신고 사유 코드
 * @return 해당 코드에 일치하는 신고 사유가 존재하면 Optional로 반환하며, 없으면 빈 Optional을 반환합니다.
 */
Optional<ReportReason> findByCode(ReportReasonCode reasonCode);
}
