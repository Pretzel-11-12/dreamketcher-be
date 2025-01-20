package pretzel.dreamketcherbe.domain.webtoon.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Genre;

@Builder
public record WebtoonGenreResDto(
    Long id
) {

    public static WebtoonGenreResDto of(Genre genre) {
        return WebtoonGenreResDto.builder()
            .id(genre.getId())
            .build();
    }
}
