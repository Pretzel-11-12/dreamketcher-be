package pretzel.dreamketcherbe.domain.webtoon.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record MyWebtoonResDto(
    Long id,
    String thumbnail,
    String title,
    String story,
    String genre
) {

    public static MyWebtoonResDto of(Webtoon webtoon, String genreName) {
        return MyWebtoonResDto.builder()
            .id(webtoon.getId())
            .thumbnail(webtoon.getThumbnail())
            .title(webtoon.getTitle())
            .story(webtoon.getStory())
            .genre(genreName)
            .build();
    }
}
