package pretzel.dreamketcherbe.domain.webtoon.dto;

import pretzel.dreamketcherbe.domain.webtoon.entity.Tag;

public record TagDto(
    Long id,
    String content
) {

    public static TagDto from(Tag tag) {
        return new TagDto(tag.getId(), tag.getContent());
    }
}
