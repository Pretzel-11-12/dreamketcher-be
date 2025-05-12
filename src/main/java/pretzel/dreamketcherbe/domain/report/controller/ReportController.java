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

    // todo: admin 확인 애노테이션 추가
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
