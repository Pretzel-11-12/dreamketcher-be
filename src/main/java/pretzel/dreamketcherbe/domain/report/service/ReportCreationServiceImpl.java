package pretzel.dreamketcherbe.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.report.dto.ReportCommand;
import pretzel.dreamketcherbe.domain.report.entity.CommentReport;
import pretzel.dreamketcherbe.domain.report.entity.EpisodeReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportReason;
import pretzel.dreamketcherbe.domain.report.repository.CommentReportRepository;
import pretzel.dreamketcherbe.domain.report.repository.EpisodeReportRepository;
import pretzel.dreamketcherbe.domain.report.repository.ReportReasonRepository;

@Service
@RequiredArgsConstructor
public class ReportCreationServiceImpl implements ReportCreationService {

    private final CommentReportRepository commentReportRepository;
    private final EpisodeReportRepository episodeReportRepository;
    private final ReportReasonRepository reportReasonRepository;

    @Override
    @Transactional
    public Long reportComment(Long commentId, ReportCommand cmd) {

        var reason = findReportReason(cmd.reasonId());

        var report = cmd.reporterMemberId() != null
            ? CommentReport.forMember(commentId, cmd.reporterMemberId(), reason,
            cmd.reasonText())
            : CommentReport.forGuest(commentId, reason, cmd.reasonText());

        return commentReportRepository.save(report).getId();
    }

    @Override
    @Transactional
    public Long reportEpisode(Long webtoonId, Long episodeId, ReportCommand cmd) {

        var reason = findReportReason(cmd.reasonId());

        var report = cmd.reporterMemberId() != null
            ? EpisodeReport.forMember(webtoonId, episodeId, cmd.reporterMemberId(), reason,
            cmd.reasonText())
            : EpisodeReport.forGuest(episodeId, reason, cmd.reasonText());

        return episodeReportRepository.save(report).getId();

    }

    private ReportReason findReportReason(Long reasonId) {
        return reportReasonRepository.findById(reasonId)
            .orElseThrow(() -> new IllegalArgumentException("신고 사유를 찾을 수 없습니다.: " + reasonId));
    }

}
