package pretzel.dreamketcherbe.domain.episode.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

@Builder
public record CreateEpisodeResDto(
    Long id
) {

    public static CreateEpisodeResDto of(Episode episode) {
        return CreateEpisodeResDto.builder()
            .id(episode.getId())
            .build();
    }
}
