package pretzel.dreamketcherbe.domain.webtoon.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWebtoonReqDto(
    @NotBlank String title,
    @NotBlank String thumbnail,
    @NotBlank String story,
    Long genreId,
    String tagsInput
) {

}
