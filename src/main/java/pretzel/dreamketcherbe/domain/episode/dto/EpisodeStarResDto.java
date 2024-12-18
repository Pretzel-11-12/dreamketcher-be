package pretzel.dreamketcherbe.domain.episode.dto;

import lombok.Builder;

@Builder
public record EpisodeStarResDto(
    Long id,
    float point
) {

    public static EpisodeStarResDto of(Long id, float point) {
        return EpisodeStarResDto.builder()
            .id(id)
            .point(point)
            .build();
    }
}
