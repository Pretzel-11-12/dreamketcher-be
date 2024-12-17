package pretzel.dreamketcherbe.domain.webtoon.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record CreateWebtoonResDto(
    Long id
) {

  public static CreateWebtoonResDto of(Webtoon webtoon) {
    return CreateWebtoonResDto.builder()
        .id(webtoon.getId())
        .build();
  }
}
