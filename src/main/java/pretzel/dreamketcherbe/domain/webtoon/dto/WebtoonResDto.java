package pretzel.dreamketcherbe.domain.webtoon.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record WebtoonResDto(
    String thumbnail,
    String member,
    String title
) {

    public static WebtoonResDto of(Webtoon webtoon) {
        return WebtoonResDto.builder()
            .thumbnail(webtoon.getThumbnail())
            .member(webtoon.getMember().getName())
            .title(webtoon.getTitle())
            .build();
    }
}
