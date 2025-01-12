package pretzel.dreamketcherbe.domain.webtoon.dto;

import java.util.List;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record SearchedWebtoonResDto(
    Long id,
    String thumbnail,
    String member,
    String title,
    List<String> genres,
    int LastEpisodeNo,
    float averageStar,
    Long numOfStars,
    String description
) {

    public static SearchedWebtoonResDto of(Webtoon webtoon, List<String> genreNames,
        Long numOfStars) {
        return SearchedWebtoonResDto.builder()
            .id(webtoon.getId())
            .thumbnail(webtoon.getThumbnail())
            .member(webtoon.getMember().getNickname())
            .title(webtoon.getTitle())
            .genres(genreNames)
            .LastEpisodeNo(webtoon.getEpisodeCount())
            .averageStar(webtoon.getAverageStar())
            .numOfStars(numOfStars)
            .description(webtoon.getDescription())
            .build();
    }
}
