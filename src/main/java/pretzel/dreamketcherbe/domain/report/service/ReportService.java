package pretzel.dreamketcherbe.domain.report.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.notification.event.CommentReportNotificationEvent;
import pretzel.dreamketcherbe.domain.notification.event.EpisodeReportNotificationEvent;
import pretzel.dreamketcherbe.domain.notification.event.RecommentReportNotificationEvent;
import pretzel.dreamketcherbe.domain.report.dto.CommentProcessResDto;
import pretzel.dreamketcherbe.domain.report.dto.EpisodeProcessResDto;
import pretzel.dreamketcherbe.domain.report.dto.RecommentProcessResDto;
import pretzel.dreamketcherbe.domain.report.dto.ReportResDto;
import pretzel.dreamketcherbe.domain.report.dto.ReportResDto.ReasonDto;
import pretzel.dreamketcherbe.domain.report.dto.ReportResDto.ReportDto;
import pretzel.dreamketcherbe.domain.report.dto.ReportResDto.ReporterDto;
import pretzel.dreamketcherbe.domain.report.entity.CommentReport;
import pretzel.dreamketcherbe.domain.report.entity.EpisodeReport;
import pretzel.dreamketcherbe.domain.report.entity.RecommentReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;
import pretzel.dreamketcherbe.domain.report.entity.ReportType;
import pretzel.dreamketcherbe.domain.report.event.CommentStatusUpdateEvent;
import pretzel.dreamketcherbe.domain.report.event.EpisodeStatusUpdateEvent;
import pretzel.dreamketcherbe.domain.report.event.RecommentStatusUpdateEvent;
import pretzel.dreamketcherbe.domain.report.repository.CommentReportRepository;
import pretzel.dreamketcherbe.domain.report.repository.EpisodeReportRepository;
import pretzel.dreamketcherbe.domain.report.repository.RecommentReportRepository;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CommentReportRepository commentReportRepository;
    private final EpisodeReportRepository episodeReportRepository;
    private final RecommentReportRepository recommentReportRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 신고 상태와 유형에 따라 댓글 및 에피소드 신고 목록을 조회하여 최신순으로 정렬하고, 페이징 처리된 결과를 반환합니다.
     *
     * @param memberId 조회를 요청한 회원의 ID
     * @param status   필터링할 신고 상태 (null이면 전체)
     * @param type     필터링할 신고 유형 (null이면 댓글과 에피소드 모두)
     * @param pageable 페이지 번호와 크기 등 페이징 정보
     * @return 페이징된 신고 목록과 전체 신고 개수를 포함한 DTO
     */
    @Transactional(readOnly = true)
    public ReportResDto getReports(
        Long memberId,
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
                report.getComment().getId(),
                new ReporterDto(report.getReporterMemberId()),
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
                report.getEpisode().getId(),
                new ReporterDto(report.getReporterMemberId()),
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

    /**
     * 에피소드 신고 처리
     */
    @Transactional
    public EpisodeProcessResDto episodeReportProcess(Long memberId, Long reportId,
        ReportStatus status, String note) {
        EpisodeReport report = episodeReportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고가 존재하지 않습니다."));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 신고입니다.");
        }

        report.reportProcess(status, memberId, note, LocalDateTime.now());

        episodeReportRepository.save(report);

        // RESOLVED 상태 변경 후, 이벤트 발행
        if (status == ReportStatus.RESOLVED) {
            EpisodeStatusUpdateEvent event = new EpisodeStatusUpdateEvent(
                reportId,
                report.getEpisode().getId(),
                memberId,
                LocalDateTime.now()
            );
            eventPublisher.publishEvent(event);
        }

        EpisodeReportNotificationEvent notificationEvent = new EpisodeReportNotificationEvent(
            report.getStatus(),
            reportId,
            report.getReporterMemberId(),
            report.getEpisode().getMember().getId(),
            report.getEpisode().getId(),
            report.getEpisode().getTitle(),
            report.getEpisode().getNo(),
            report.getEpisode().getWebtoon().getTitle(),
            LocalDateTime.now()
        );
        eventPublisher.publishEvent(notificationEvent);

        return EpisodeProcessResDto.from(report);
    }

    /**
     * 댓글 신고 처리
     */
    @Transactional
    public CommentProcessResDto commentReportProcess(Long memberId, Long reportId,
        ReportStatus status, String note) {
        CommentReport report = commentReportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고가 존재하지 않습니다."));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 신고입니다.");
        }

        report.reportProcess(status, LocalDateTime.now(), memberId, note);

        commentReportRepository.save(report);

        if (status == ReportStatus.RESOLVED) {
            // 댓글 상태 전환 이벤트 발행
            CommentStatusUpdateEvent event = new CommentStatusUpdateEvent(
                report.getComment().getId(),
                reportId,
                memberId,
                LocalDateTime.now()
            );
            eventPublisher.publishEvent(event);
        }

        CommentReportNotificationEvent notificationEvent = new CommentReportNotificationEvent(
            report.getStatus(),
            reportId,
            report.getReporterMemberId(),
            report.getComment().getMember().getId(),
            report.getComment().getEpisode().getTitle(),
            report.getComment().getEpisode().getNo(),
            report.getComment().getWebtoon().getTitle(),
            report.getComment().getId(),
            report.getAdminNote(),
            LocalDateTime.now()
        );
        eventPublisher.publishEvent(notificationEvent);

        return CommentProcessResDto.from(report);
    }

    /**
     * 답글 신고 처리
     */
    @Transactional
    public RecommentProcessResDto recommentReportProcess(
        Long memberId, Long reportId, ReportStatus status, String note
    ) {
        RecommentReport report = recommentReportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고가 존재하지 않습니다."));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 신고입니다.");
        }

        report.reportProcess(status, LocalDateTime.now(), memberId, note);

        recommentReportRepository.save(report);

        if (status == ReportStatus.RESOLVED) {
            RecommentStatusUpdateEvent event = new RecommentStatusUpdateEvent(
                reportId,
                report.getRecomment().getId(),
                memberId,
                LocalDateTime.now()
            );
            eventPublisher.publishEvent(event);
        }

        RecommentReportNotificationEvent notificationEvent = new RecommentReportNotificationEvent(
            report.getStatus(),
            reportId,
            report.getReporterMemberId(),
            report.getRecomment().getMember().getId(),
            report.getRecomment().getEpisode().getTitle(),
            report.getRecomment().getEpisode().getNo(),
            report.getRecomment().getWebtoon().getTitle(),
            report.getRecomment().getComment().getContent(),
            report.getRecomment().getId(),
            report.getAdminNote(),
            LocalDateTime.now()
        );
        eventPublisher.publishEvent(notificationEvent);

        return RecommentProcessResDto.from(report);
    }

}
