package pretzel.dreamketcherbe.domain.episode.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.episode.entity.EpisodeLike;
import pretzel.dreamketcherbe.domain.episode.entity.EpisodeStar;

@Builder
public record MemberEpisodeLikeAndStarResDto(
    Long episodeStarId,
    float point,
    Long episodeLikeId
) {

    public static MemberEpisodeLikeAndStarResDto of(EpisodeStar episodeStar,
        EpisodeLike episodeLike) {
        return MemberEpisodeLikeAndStarResDto.builder()
            .episodeStarId(episodeStar.getId())
            .point(episodeStar.getPoint())
            .episodeLikeId(episodeLike.getId())
            .build();
    }

}
