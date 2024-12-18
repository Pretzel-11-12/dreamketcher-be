package pretzel.dreamketcherbe.domain.webtoon.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWebtoonReqDto(
    @NotBlank String title,
    @NotBlank String thumbnail,
    @NotBlank String prologue,
    @NotBlank String story,
    @NotBlank String description
) {

}
