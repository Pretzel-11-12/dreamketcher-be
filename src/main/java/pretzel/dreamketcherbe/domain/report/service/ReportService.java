package pretzel.dreamketcherbe.domain.report.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.comment.repository.CommentRepository;
import pretzel.dreamketcherbe.domain.report.dto.ReportResDto;
import pretzel.dreamketcherbe.domain.report.dto.ReportResDto.ReasonDto;
import pretzel.dreamketcherbe.domain.report.dto.ReportResDto.ReportDto;
import pretzel.dreamketcherbe.domain.report.dto.ReportResDto.ReporterDto;
import pretzel.dreamketcherbe.domain.report.entity.CommentReport;
import pretzel.dreamketcherbe.domain.report.entity.EpisodeReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;
import pretzel.dreamketcherbe.domain.report.entity.ReportType;
import pretzel.dreamketcherbe.domain.report.repository.CommentReportRepository;
import pretzel.dreamketcherbe.domain.report.repository.EpisodeReportRepository;

@Service
public class ReportService {

    private final CommentReportRepository commentReportRepository;
    private final EpisodeReportRepository episodeReportRepository;
    private final CommentRepository commentRepository;

    public ReportService(CommentReportRepository commentReportRepository,
        EpisodeReportRepository episodeReportRepository, CommentRepository commentRepository) {
        this.commentReportRepository = commentReportRepository;
        this.episodeReportRepository = episodeReportRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    public ReportResDto getReports(
        ReportStatus status,
        ReportType type,
        Pageable pageable
    ) {
        List<ReportDto> reports = new ArrayList<>();
        long totalCount = 0;

        // 댓글 신고 조회
        if (type == null || type == ReportType.COMMENT) {
            Page<CommentReport> commentReportPage = getCommentReports(status, pageable);
            reports.addAll(mapCommentReportsToDto(commentReportPage.getContent()));
            totalCount += commentReportPage.getTotalElements();
        }

        // 에피소드 신고 조회
        if (type == null || type == ReportType.EPISODE) {
            Page<EpisodeReport> episodeReportPage = getEpisodeReports(status, pageable);
            reports.addAll(mapEpisodeReportsToDto(episodeReportPage.getContent()));
            totalCount += episodeReportPage.getTotalElements();
        }

        // 최신순 정렬
        reports.sort((r1, r2) -> r2.reportedAt().compareTo(r1.reportedAt()));

        int start = Math.min(reports.size(), pageable.getPageNumber() * pageable.getPageSize());
        int end = Math.min(reports.size(), (pageable.getPageNumber() + 1) * pageable.getPageSize());
        reports = reports.subList(start, end);

        return new ReportResDto(
            reports,
            pageable.getPageNumber(),
            pageable.getPageSize(),
            totalCount
        );
    }

    private Page<CommentReport> getCommentReports(ReportStatus status, Pageable pageable) {
        if (status == null) {
            return commentReportRepository.findAll(pageable);
        }
        return commentReportRepository.findByStatus(status, pageable);
    }

    private Page<EpisodeReport> getEpisodeReports(ReportStatus status, Pageable pageable) {
        if (status == null) {
            return episodeReportRepository.findAll(pageable);
        }
        return episodeReportRepository.findByStatus(status, pageable);
    }

    private List<ReportDto> mapCommentReportsToDto(List<CommentReport> reports) {
        return reports.stream()
            .map(report -> new ReportDto(
                report.getId(),
                ReportType.COMMENT,
                report.getCommentId(),
                new ReporterDto(report.getReporterMemberId(), report.getReporterIp()),
                new ReasonDto(report.getReason().getCode(), report.getReason().getDescription()),
                report.getReasonText(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getProcessedBy(),
                report.getProcessedAt(),
                report.getAdminNote()
            ))
            .toList();
    }

    private List<ReportDto> mapEpisodeReportsToDto(List<EpisodeReport> reports) {
        return reports.stream()
            .map(report -> new ReportDto(
                report.getId(),
                ReportType.EPISODE,
                report.getEpisodeId(),
                new ReporterDto(report.getReporterMemberId(), report.getReporterIp()),
                new ReasonDto(report.getReason().getCode(), report.getReason().getDescription()),
                report.getReasonText(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getProcessedBy(),
                report.getProcessedAt(),
                report.getAdminNote()
            ))
            .toList();
    }
}
