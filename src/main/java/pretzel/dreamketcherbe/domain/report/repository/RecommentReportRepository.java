package pretzel.dreamketcherbe.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.report.entity.RecommentReport;

public interface RecommentReportRepository extends JpaRepository<RecommentReport, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
          FROM RecommentReport c
         WHERE c.recomment.id        = :recommentId
           AND c.reporterMemberId = :memberId
        """)
    Boolean existsByRecommentIdAndMemberId(Long recommentId, Long memberId);

}
