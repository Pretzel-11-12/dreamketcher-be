package pretzel.dreamketcherbe.domain.webtoon.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CreateWebtoonReqDto(
    @NotBlank String title,
    String thumbnail,
    List<String> prologue,
    @NotBlank String story,
    @NotBlank String description
) {

}
