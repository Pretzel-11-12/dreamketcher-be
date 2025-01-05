package pretzel.dreamketcherbe.domain.webtoon.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record WebtoonResDto(
    Long id,
    String thumbnail,
    String member,
    String title
) {

    public static WebtoonResDto of(Webtoon webtoon) {
        return WebtoonResDto.builder()
            .id(webtoon.getId())
            .thumbnail(webtoon.getThumbnail())
            .member(webtoon.getMember().getName())
            .title(webtoon.getTitle())
            .build();
    }
}
