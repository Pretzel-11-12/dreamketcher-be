package pretzel.dreamketcherbe.domain.member.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;

@Builder
public record UpdateStorageFolderResDto(
    Long id,
    String folderName
) {
    public static UpdateStorageFolderResDto of(StorageFolder storageFolder) {
        return UpdateStorageFolderResDto.builder()
            .id(storageFolder.getId())
            .folderName(storageFolder.getName())
            .build();
    }
}
