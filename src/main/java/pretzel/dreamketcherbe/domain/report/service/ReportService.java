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

    /**
     * ReportService의 인스턴스를 생성하고 필요한 리포지토리 의존성을 주입합니다.
     *
     * @param commentReportRepository 댓글 신고 관련 데이터 접근을 위한 리포지토리
     * @param episodeReportRepository 에피소드 신고 관련 데이터 접근을 위한 리포지토리
     * @param commentRepository 댓글 데이터 접근을 위한 리포지토리
     */
    public ReportService(CommentReportRepository commentReportRepository,
        EpisodeReportRepository episodeReportRepository, CommentRepository commentRepository) {
        this.commentReportRepository = commentReportRepository;
        this.episodeReportRepository = episodeReportRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * 신고 상태, 유형, 페이지 정보를 기준으로 댓글 및 에피소드 신고 목록을 조회하여 통합된 DTO 리스트로 반환합니다.
     *
     * @param status 조회할 신고 상태 (null이면 전체)
     * @param type 조회할 신고 유형(COMMENT, EPISODE, 또는 null로 전체)
     * @param pageable 페이지 번호와 크기 등 페이징 정보
     * @return 필터링 및 페이징된 신고 목록과 전체 개수를 포함하는 ReportResDto
     */
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

    /**
     * 주어진 상태에 따라 댓글 신고 목록을 페이지 단위로 조회합니다.
     *
     * @param status 필터링할 신고 상태. null이면 모든 상태의 신고를 조회합니다.
     * @param pageable 페이지 정보
     * @return 조회된 댓글 신고의 페이지 객체
     */
    private Page<CommentReport> getCommentReports(ReportStatus status, Pageable pageable) {
        if (status == null) {
            return commentReportRepository.findAll(pageable);
        }
        return commentReportRepository.findByStatus(status, pageable);
    }

    /**
     * 에피소드 신고 목록을 상태별로 조회하여 페이지로 반환합니다.
     *
     * @param status 조회할 신고 상태. null이면 모든 상태의 신고를 조회합니다.
     * @param pageable 페이지네이션 정보
     * @return 상태에 따라 필터링된 에피소드 신고의 페이지
     */
    private Page<EpisodeReport> getEpisodeReports(ReportStatus status, Pageable pageable) {
        if (status == null) {
            return episodeReportRepository.findAll(pageable);
        }
        return episodeReportRepository.findByStatus(status, pageable);
    }

    /**
     * CommentReport 엔티티 목록을 ReportDto 리스트로 변환합니다.
     *
     * 각 댓글 신고 정보를 공통 응답 DTO 형식(ReportDto)으로 매핑하여 반환합니다.
     *
     * @param reports 변환할 CommentReport 엔티티 리스트
     * @return 변환된 ReportDto 리스트
     */
    private List<ReportDto> mapCommentReportsToDto(List<CommentReport> reports) {
        return reports.stream()
            .map(report -> new ReportDto(
                report.getId(),
                ReportType.COMMENT,
                report.getCommentId(),
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
     * EpisodeReport 엔티티 목록을 ReportDto 리스트로 변환합니다.
     *
     * 각 에피소드 신고 정보를 공통 응답 DTO 형식(ReportDto)으로 매핑하여 반환합니다.
     *
     * @param reports 변환할 EpisodeReport 엔티티 리스트
     * @return 변환된 ReportDto 리스트
     */
    private List<ReportDto> mapEpisodeReportsToDto(List<EpisodeReport> reports) {
        return reports.stream()
            .map(report -> new ReportDto(
                report.getId(),
                ReportType.EPISODE,
                report.getEpisodeId(),
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
}
