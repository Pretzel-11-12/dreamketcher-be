package pretzel.dreamketcherbe.domain.ranking.dto;

import lombok.Builder;

@Builder
public record RankingResDto(
        Long id,
        String title,
        String member,
        String description,
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
            .description(data.getDescription())
            .thumbnail(data.getThumbnail())
            .genre(data.getGenre())
            .lastEpisode(data.getLastEpisode())
            .averageStar(data.getAverageStar())
            .numOfStars(data.getNumOfStars())
            .build();
    }
}
