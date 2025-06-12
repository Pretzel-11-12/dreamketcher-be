package pretzel.dreamketcherbe.domain.report.dto;

import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

public record RecommentProcessReqDto(
    ReportStatus status,
    String adminNote
) {

}
