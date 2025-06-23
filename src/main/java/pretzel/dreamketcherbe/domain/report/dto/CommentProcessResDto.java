package pretzel.dreamketcherbe.domain.report.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.report.entity.CommentReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

@Builder
public record CommentProcessResDto(
    Long id,
    ReportStatus status,
    LocalDateTime processedAt,
    Long processedBy,
    String adminNote
) {

    public static CommentProcessResDto from(CommentReport report) {
        return CommentProcessResDto.builder()
            .id(report.getId())
            .status(report.getStatus())
            .processedAt(report.getProcessedAt())
            .processedBy(report.getProcessedBy())
            .build();
    }
}
