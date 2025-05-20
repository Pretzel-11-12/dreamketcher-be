package pretzel.dreamketcherbe.domain.member.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.member.entity.StorageItem;

@Builder
public record CreateStorageItemResDto(
     Long StorageItemId
) {
    public static CreateStorageItemResDto of(StorageItem item) {
        return CreateStorageItemResDto.builder()
            .StorageItemId(item.getId())
            .build();
    }
}
