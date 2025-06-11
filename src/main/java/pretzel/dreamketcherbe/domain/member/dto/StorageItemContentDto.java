package pretzel.dreamketcherbe.domain.member.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.member.entity.StorageItem;

@Builder
public record StorageItemContentDto(
    Long webtoonId,
    String title,
    String thumbnail,
    String authorNickname
) {
    public static StorageItemContentDto of(StorageItem item) {
        return StorageItemContentDto.builder()
            .webtoonId(item.getWebtoon().getId())
            .title(item.getWebtoon().getTitle())
            .thumbnail(item.getWebtoon().getThumbnail())
            .authorNickname(item.getWebtoon().getMember().getNickname())
            .build();
    }
}
