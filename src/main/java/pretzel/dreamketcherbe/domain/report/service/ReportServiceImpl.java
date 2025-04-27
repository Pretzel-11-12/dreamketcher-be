package pretzel.dreamketcherbe.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.report.entity.CommentReport;
import pretzel.dreamketcherbe.domain.report.entity.EpisodeReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportReason;
import pretzel.dreamketcherbe.domain.report.repository.CommentReportRepository;
import pretzel.dreamketcherbe.domain.report.repository.EpisodeReportRepository;
import pretzel.dreamketcherbe.domain.report.repository.ReportReasonRepository;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final CommentReportRepository commentReportRepository;
    private final EpisodeReportRepository episodeReportRepository;
    private final ReportReasonRepository reportReasonRepository;

    @Override
    @Transactional
    public Long reportComment(Long commentId, ReportCommand cmd) {

        var reason = findReportReason(cmd.getReasonId());

        var report = cmd.getReporterMemberId() != null
            ? CommentReport.forMember(commentId, cmd.getReporterMemberId(), reason,
            cmd.getReasonText())
            : CommentReport.forGuest(commentId, cmd.getReporterIp(), reason, cmd.getReasonText());

        return commentReportRepository.save(report).getId();
    }

    @Override
    @Transactional
    public Long reportEpisode(Long episodeId, ReportCommand cmd) {

        var reason = findReportReason(cmd.getReasonId());

        var report = cmd.getReporterMemberId() != null
            ? EpisodeReport.forMember(episodeId, cmd.getReporterMemberId(), reason,
            cmd.getReasonText())
            : EpisodeReport.forGuest(episodeId, cmd.getReporterIp(), reason, cmd.getReasonText());

        return episodeReportRepository.save(report).getId();

    }

    private ReportReason findReportReason(Long reasonId) {
        return reportReasonRepository.findById(reasonId)
            .orElseThrow(() -> new IllegalArgumentException("신고 사유를 찾을 수 없습니다.: " + reasonId));
    }

}
