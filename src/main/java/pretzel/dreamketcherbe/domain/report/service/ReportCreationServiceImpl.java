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

    /**
     * 지정된 댓글에 대한 신고를 생성하고 신고 ID를 반환합니다.
     *
     * @param commentId 신고할 댓글의 ID
     * @param cmd 신고 정보가 담긴 명령 객체
     * @return 생성된 댓글 신고의 ID
     *
     * @throws IllegalArgumentException 신고 사유가 존재하지 않을 경우 발생합니다.
     */
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

    /**
     * 에피소드에 대한 신고를 생성하고 신고 ID를 반환합니다.
     *
     * @param episodeId 신고 대상 에피소드의 ID
     * @param cmd 신고 정보와 사유를 담은 명령 객체
     * @return 생성된 에피소드 신고의 ID
     * @throws IllegalArgumentException 존재하지 않는 신고 사유 ID가 전달된 경우
     */
    @Override
    @Transactional
    public Long reportEpisode(Long episodeId, ReportCommand cmd) {

        var reason = findReportReason(cmd.reasonId());

        var report = cmd.reporterMemberId() != null
            ? EpisodeReport.forMember(episodeId, cmd.reporterMemberId(), reason,
            cmd.reasonText())
            : EpisodeReport.forGuest(episodeId, reason, cmd.reasonText());

        return episodeReportRepository.save(report).getId();

    }

    /**
     * 주어진 ID에 해당하는 신고 사유 엔티티를 조회합니다.
     *
     * @param reasonId 조회할 신고 사유의 ID
     * @return 조회된 신고 사유 엔티티
     * @throws IllegalArgumentException 해당 ID의 신고 사유가 존재하지 않을 경우 발생
     */
    private ReportReason findReportReason(Long reasonId) {
        return reportReasonRepository.findById(reasonId)
            .orElseThrow(() -> new IllegalArgumentException("신고 사유를 찾을 수 없습니다.: " + reasonId));
    }

}
