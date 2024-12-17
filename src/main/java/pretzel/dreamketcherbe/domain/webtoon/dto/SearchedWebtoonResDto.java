package pretzel.dreamketcherbe.domain.webtoon.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record SearchedWebtoonResDto(
    String thumbnail,
    String member,
    String title,
    String description
) {

    public static SearchedWebtoonResDto of(Webtoon webtoon) {
        return SearchedWebtoonResDto.builder()
            .thumbnail(webtoon.getThumbnail())
            .member(webtoon.getMember().getNickname())
            .title(webtoon.getTitle())
            .description(webtoon.getDescription())
            .build();
    }
}
