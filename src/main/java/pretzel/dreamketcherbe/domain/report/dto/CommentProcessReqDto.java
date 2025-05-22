package pretzel.dreamketcherbe.domain.report.dto;

import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

public record CommentProcessReqDto(
    Long id,
    ReportStatus status,
    Long processedBy,
    String adminNote,
    String processedAt
) {

}
