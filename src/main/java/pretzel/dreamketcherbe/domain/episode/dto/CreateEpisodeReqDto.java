package pretzel.dreamketcherbe.domain.episode.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateEpisodeReqDto(
    @NotBlank String title,
    @NotBlank String thumbnail,
    @NotBlank String content,
    @NotBlank String authorNote
) {

}
