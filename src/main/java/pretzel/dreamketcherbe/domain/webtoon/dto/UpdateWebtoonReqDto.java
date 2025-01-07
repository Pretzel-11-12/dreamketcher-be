package pretzel.dreamketcherbe.domain.webtoon.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record UpdateWebtoonReqDto(
    @NotBlank String title,
    @NotBlank String thumbnail,
    @NotBlank List<String> prologue,
    @NotBlank String story,
    @NotBlank String description
) {

}
