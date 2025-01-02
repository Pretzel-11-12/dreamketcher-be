package pretzel.dreamketcherbe.domain.episode.dto;

import jakarta.validation.constraints.NotNull;

public record EpisodeStarReqDto(
    Long episodeId,
    Long memberId,
    @NotNull float point
) {

}
