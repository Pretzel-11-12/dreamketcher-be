package pretzel.dreamketcherbe.domain.episode.dto;

import jakarta.validation.constraints.NotEmpty;

public record EpisodeStarReqDto(
    Long episodeId,
    Long memberId,
    @NotEmpty float point
) {

}
