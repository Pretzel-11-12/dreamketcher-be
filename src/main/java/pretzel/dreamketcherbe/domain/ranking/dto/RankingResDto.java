package pretzel.dreamketcherbe.domain.ranking.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RankingResDto(
        String title,
        String thumbnail,
        List<String> genres,
        int lastEpisode,
        float averageStar,
        int numOfStars
) {
    public static RankingResDto of(WebtoonPopularityDataDto data) {
        return RankingResDto.builder()
            .title(data.getTitle())
            .thumbnail(data.getThumbnail())
            .genres(data.getGenres())
            .lastEpisode(data.getLastEpisode())
            .averageStar(data.getAverageStar())
            .numOfStars(data.getNumOfStars())
            .build();
    }
}
