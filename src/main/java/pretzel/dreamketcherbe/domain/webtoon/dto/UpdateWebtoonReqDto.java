package pretzel.dreamketcherbe.domain.webtoon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UpdateWebtoonReqDto(
    @NotBlank String title,
    @NotBlank String thumbnail,
    @NotBlank String story
) {

}
