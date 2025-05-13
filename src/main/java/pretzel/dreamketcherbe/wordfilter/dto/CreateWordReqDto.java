package pretzel.dreamketcherbe.wordfilter.dto;

import jakarta.validation.constraints.NotNull;

public record CreateWordReqDto(
    @NotNull String word
) {

}
