package pretzel.dreamketcherbe.domain.ranking.dto;

import lombok.Builder;

@Builder
public record RankingResDto(
        Long id,
        String title,
        String member,
        String story,
        String thumbnail,
        String genre,
        int lastEpisode,
        float averageStar,
        Long numOfStars
) {
    public static RankingResDto of(WebtoonPopularityDataDto data) {
        return RankingResDto.builder()
            .id(data.getId())
            .title(data.getTitle())
            .member(data.getMember())
            .story(data.getStory())
            .thumbnail(data.getThumbnail())
            .genre(data.getGenre())
            .lastEpisode(data.getLastEpisode())
            .averageStar(data.getAverageStar())
            .numOfStars(data.getNumOfStars())
            .build();
    }
}
