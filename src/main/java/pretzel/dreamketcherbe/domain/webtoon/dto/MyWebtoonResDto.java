package pretzel.dreamketcherbe.domain.webtoon.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record MyWebtoonResDto(
    Long id,
    String thumbnail,
    String title,
    String prologue,
    String story,
    String description,
    String genre
) {

    public static MyWebtoonResDto of(Webtoon webtoon, String genreName) {
        return MyWebtoonResDto.builder()
            .id(webtoon.getId())
            .thumbnail(webtoon.getThumbnail())
            .title(webtoon.getTitle())
            .prologue(webtoon.getPrologue())
            .story(webtoon.getStory())
            .description(webtoon.getDescription())
            .genre(genreName)
            .build();
    }
}
