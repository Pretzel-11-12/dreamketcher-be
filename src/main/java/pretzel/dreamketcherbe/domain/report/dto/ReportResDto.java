package pretzel.dreamketcherbe.domain.report.dto;

import java.time.LocalDateTime;
import java.util.List;
import pretzel.dreamketcherbe.domain.report.entity.ReportReasonCode;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;
import pretzel.dreamketcherbe.domain.report.entity.ReportType;

public record ReportResDto(
    List<ReportDto> reports,
    int page,
    int size,
    long totalCount
) {

    public record ReportDto(
        Long reportId,
        ReportType type,
        Long targetId,
        ReporterDto reporter,
        ReasonDto reason,
        String reasonText,
        ReportStatus status,
        LocalDateTime reportedAt,
        Long processedBy,
        LocalDateTime processedAt,
        String adminNote
    ) {

    }

    public record ReporterDto(
        Long memberId,
        String ip
    ) {

    }

    public record ReasonDto(
        ReportReasonCode code,
        String description
    ) {

    }
}
