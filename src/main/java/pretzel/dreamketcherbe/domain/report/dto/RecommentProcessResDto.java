package pretzel.dreamketcherbe.domain.report.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.report.entity.RecommentReport;

@Builder
public record RecommentProcessResDto(
    Long id,
    String status,
    Long processedBy,
    String adminNote,
    LocalDateTime processedAt
) {

    public static RecommentProcessResDto from(RecommentReport report) {
        return RecommentProcessResDto.builder()
            .id(report.getId())
            .status(report.getStatus().name())
            .processedBy(report.getProcessedBy())
            .adminNote(report.getAdminNote())
            .processedAt(report.getProcessedAt())
            .build();
    }

}
