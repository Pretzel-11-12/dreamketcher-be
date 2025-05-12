package pretzel.dreamketcherbe.domain.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.common.annotation.Admin;
import pretzel.dreamketcherbe.domain.report.dto.ReportResDto;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;
import pretzel.dreamketcherbe.domain.report.entity.ReportType;
import pretzel.dreamketcherbe.domain.report.service.ReportService;

@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 관리자가 신고 목록을 필터링 및 페이징하여 조회합니다.
     *
     * @param status 조회할 신고 상태(기본값: "PENDING")
     * @param type   조회할 신고 유형(선택 사항)
     * @param page   페이지 번호(기본값: 0)
     * @param size   페이지 크기(기본값: 20)
     * @return 필터링 및 페이징된 신고 목록 응답
     */
    @GetMapping
    public ResponseEntity<ReportResDto> getReports(
        @Admin Long memberId,
        @RequestParam(required = false, defaultValue = "PENDING") String status,
        @RequestParam(required = false) String type,
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "20") int size
    ) {
        ReportStatus reportStatus = ReportStatus.valueOf(status);

        ReportType reportType = type != null
            ? ReportType.valueOf(type.toUpperCase())
            : null;

        PageRequest pageRequest = PageRequest.of(
            page,
            size,
            Sort.by(Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
            reportService.getReports(memberId, reportStatus, reportType, pageRequest));
    }
}
