package pretzel.dreamketcherbe.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.report.entity.EpisodeReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

public interface EpisodeReportRepository extends JpaRepository<EpisodeReport, Long> {

    Page<EpisodeReport> findByStatus(ReportStatus status, Pageable pageable);

    @Query("""
        SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
          FROM EpisodeReport e
         WHERE e.episodeId        = :episodeId
           AND e.reporterMemberId = :memberId
        """)
    Boolean existsByEpisodeIdAndMemberId(@Param("episodeId") Long episodeId,
        @Param("memberId") Long memberId);

    // RESOVED 에피소드
    @Query("SELECT e FROM EpisodeReport e WHERE e.status = 'RESOLVED' AND e.episodeId = :episodeId")
    EpisodeReport findResolvedEpisodeReportByEpisodeId(@Param("episodeId") Long episodeId);
}
