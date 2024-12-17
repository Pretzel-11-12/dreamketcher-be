package pretzel.dreamketcherbe.domain.episode.dto;

public record CreateEpisodeLikeResDto(
    Long episodeId,
    int likeCount
) {

    public static CreateEpisodeLikeResDto of(Long episodeId, int likeCount) {
        return new CreateEpisodeLikeResDto(episodeId, likeCount);
    }
}
