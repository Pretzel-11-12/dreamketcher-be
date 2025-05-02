package pretzel.dreamketcherbe.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.report.entity.CommentReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    /**
 * 지정된 신고 상태에 해당하는 댓글 신고 목록을 페이지 단위로 조회합니다.
 *
 * @param status 조회할 신고 상태
 * @param pageable 페이지 및 정렬 정보를 담은 객체
 * @return 조건에 맞는 댓글 신고의 페이지 결과
 */
Page<CommentReport> findByStatus(ReportStatus status, Pageable pageable);
}
