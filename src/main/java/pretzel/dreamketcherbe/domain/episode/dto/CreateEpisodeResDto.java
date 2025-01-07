package pretzel.dreamketcherbe.domain.episode.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

@Builder
public record CreateEpisodeResDto(
    Long id,
    int no
) {

    public static CreateEpisodeResDto of(Episode episode) {
        return CreateEpisodeResDto.builder()
            .id(episode.getId())
            .no(episode.getNo())
            .build();
    }
}
