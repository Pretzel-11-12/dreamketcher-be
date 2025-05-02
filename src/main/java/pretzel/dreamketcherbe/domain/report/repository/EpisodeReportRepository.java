package pretzel.dreamketcherbe.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.report.entity.EpisodeReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

public interface EpisodeReportRepository extends JpaRepository<EpisodeReport, Long> {

    /**
 * 지정된 신고 상태에 따라 에피소드 신고 목록을 페이지 단위로 조회합니다.
 *
 * @param status 조회할 신고 상태
 * @param pageable 페이지 및 정렬 정보
 * @return 해당 상태의 에피소드 신고 목록 페이지
 */
Page<EpisodeReport> findByStatus(ReportStatus status, Pageable pageable);
}
