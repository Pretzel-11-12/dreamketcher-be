package pretzel.dreamketcherbe.domain.report.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.report.entity.EpisodeReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

@Builder
public record EpisodeProcessResDto(
    Long id,
    ReportStatus status,
    Long processedBy,
    String adminNote,
    LocalDateTime processedAt
) {

    public static EpisodeProcessResDto from(EpisodeReport report) {
        return EpisodeProcessResDto.builder()
            .id(report.getId())
            .status(report.getStatus())
            .processedBy(report.getProcessedBy())
            .adminNote(report.getAdminNote())
            .processedAt(report.getProcessedAt())
            .build();
    }
}
