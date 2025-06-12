package pretzel.dreamketcherbe.domain.report.dto;

import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

public record CommentProcessReqDto(
    ReportStatus status,
    Long processedBy,
    String adminNote,
    String processedAt
) {

}
