package pretzel.dreamketcherbe.domain.webtoon.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateWebtoonReqDto(
    @NotBlank String title,
    @NotBlank String thumbnail,
    @NotBlank String story,
    String tagsInput
) {

}
