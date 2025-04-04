package pretzel.dreamketcherbe.domain.webtoon.dto;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record MyWebtoonResDto(
    Long id,
    String thumbnail,
    String title,
    String story,
    String genre,
    List<TagDto> tags
) {

    public static MyWebtoonResDto of(Webtoon webtoon, String genreName) {
        List<TagDto> tagDtos = webtoon.getWebtoonTags()
            .stream()
            .map(wt -> TagDto.from(wt.getTag()))
            .collect(Collectors.toList());

        return MyWebtoonResDto.builder()
            .id(webtoon.getId())
            .thumbnail(webtoon.getThumbnail())
            .title(webtoon.getTitle())
            .story(webtoon.getStory())
            .genre(genreName)
            .tags(tagDtos)
            .build();
    }
}
