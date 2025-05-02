package pretzel.dreamketcherbe.domain.report.dto;

import jakarta.validation.constraints.NotNull;

public record ReportEpisodeReqDto(
    @NotNull Long reasonId,
    String reasonText
) {

}
