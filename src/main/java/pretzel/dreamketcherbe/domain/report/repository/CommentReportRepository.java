package pretzel.dreamketcherbe.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.report.entity.CommentReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    Page<CommentReport> findByStatus(ReportStatus status, Pageable pageable);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
          FROM CommentReport c
         WHERE c.commentId        = :commentId
           AND c.reporterMemberId = :memberId
        """)
    Boolean existsByCommentIdAndMemberId(Long commentId, Long memberId);
}
