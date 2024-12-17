package pretzel.dreamketcherbe.domain.episode.dto;

import jakarta.validation.constraints.NotNull;

public record CreateEpisodeLikeReqDto(
    @NotNull Long memberId
) {

}
