package pretzel.dreamketcherbe.domain.report.dto;

import jakarta.validation.constraints.NotNull;

public record ReportCommentReqDto(
    @NotNull Long reasonId,
    String reasonText
) {

}
