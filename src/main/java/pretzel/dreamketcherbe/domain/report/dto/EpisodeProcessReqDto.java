package pretzel.dreamketcherbe.domain.report.dto;

import java.time.LocalDateTime;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

public record EpisodeProcessReqDto(
    Long id,
    ReportStatus status,
    Long processedBy,
    String adminNote,
    LocalDateTime processedAt
) {

}
