package pretzel.dreamketcherbe.domain.webtoon.dto;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record WebtoonDetailResDto(
    Long webtoonId,
    String webtoonTitle,
    String webtoonThumbnail,
    String webtoonStory,
    String authorNickname,
    int interestCount,
    String genreName,
    List<TagDto> tags
) {

    public static WebtoonDetailResDto from(Webtoon webtoon) {
        List<TagDto> tagDtos = webtoon.getWebtoonTags()
            .stream()
            .map(wt -> TagDto.from(wt.getTag()))
            .collect(Collectors.toList());

        return WebtoonDetailResDto.builder()
            .webtoonId(webtoon.getId())
            .webtoonTitle(webtoon.getTitle())
            .webtoonThumbnail(webtoon.getThumbnail())
            .webtoonStory(webtoon.getStory())
            .authorNickname(webtoon.getMember().getNickname())
            .interestCount(webtoon.getInterestCount())
            .genreName(webtoon.getGenre().getName())
            .tags(tagDtos)
            .build();
    }
}

