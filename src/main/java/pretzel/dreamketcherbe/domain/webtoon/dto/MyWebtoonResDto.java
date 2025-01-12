package pretzel.dreamketcherbe.domain.webtoon.dto;

import java.util.List;
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
    List<String> genres
) {

    public static MyWebtoonResDto of(Webtoon webtoon, List<String> genreNames) {
        return MyWebtoonResDto.builder()
            .id(webtoon.getId())
            .thumbnail(webtoon.getThumbnail())
            .title(webtoon.getTitle())
            .prologue(webtoon.getPrologue())
            .story(webtoon.getStory())
            .description(webtoon.getDescription())
            .genres(genreNames)
            .build(
            );
    }
}
