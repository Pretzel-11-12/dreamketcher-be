package pretzel.dreamketcherbe.domain.webtoon.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonGenre;

@Builder
public record CreateWebtoonGenreResDto(
    Long id
) {

    public static CreateWebtoonGenreResDto of(WebtoonGenre webtoonGenre) {
        return CreateWebtoonGenreResDto.builder()
            .id(webtoonGenre.getId())
            .build();
    }
}
