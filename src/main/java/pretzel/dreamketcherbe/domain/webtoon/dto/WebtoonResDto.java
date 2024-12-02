package pretzel.dreamketcherbe.domain.webtoon.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record WebtoonResDto(
        String thumnail,
        String author,
        String title
) {
    public static WebtoonResDto of(Webtoon webtoon) {
        return WebtoonResDto.builder()
                .thumnail(webtoon.getThumnail())
                .author(webtoon.getAuthor().getName())
                .title(webtoon.getTitle())
                .build();
    }
}
