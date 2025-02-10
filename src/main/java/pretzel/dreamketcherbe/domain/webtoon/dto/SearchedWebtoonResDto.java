package pretzel.dreamketcherbe.domain.webtoon.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record SearchedWebtoonResDto(
    Long id,
    String thumbnail,
    String member,
    String title,
    String genre,
    int lastEpisode,
    float averageStar,
    Long numOfStars
) {

    public static SearchedWebtoonResDto of(Webtoon webtoon, String genreName,
        Long numOfStars) {
        return SearchedWebtoonResDto.builder()
            .id(webtoon.getId())
            .thumbnail(webtoon.getThumbnail())
            .member(webtoon.getMember().getNickname())
            .title(webtoon.getTitle())
            .genre(genreName)
            .lastEpisode(webtoon.getEpisodeCount())
            .averageStar(webtoon.getAverageStar())
            .numOfStars(numOfStars)
            .build();
    }
}
