package pretzel.dreamketcherbe.domain.webtoon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateWebtoonReqDto(
    @NotBlank String title,
    @NotBlank String thumbnail,
    @NotEmpty String prologue,
    @NotBlank String story,
    @NotBlank String description
) {

}
