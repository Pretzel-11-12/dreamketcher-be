package pretzel.dreamketcherbe.domain.episode.dto;

import lombok.Builder;

@Builder
public record CreateEpisodeLikeResDto(
    Long episodeId,
    int likeCount
) {

    public static CreateEpisodeLikeResDto of(Long episodeId, int likeCount) {
        return CreateEpisodeLikeResDto.builder()
            .episodeId(episodeId)
            .likeCount(likeCount)
            .build();
    }
}
