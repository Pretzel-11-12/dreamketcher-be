package pretzel.dreamketcherbe.domain.webtoon.dto;

import java.util.List;

public record WebtoonsByTagResDto(
    Long id,
    String content,
    List<WebtoonDetailResDto> webtoons
) {

}
