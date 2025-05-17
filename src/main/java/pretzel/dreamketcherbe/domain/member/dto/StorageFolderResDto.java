package pretzel.dreamketcherbe.domain.member.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;

@Builder
public record StorageFolderResDto(
    Long folderId,
    String folderName
) {
    public static StorageFolderResDto of(StorageFolder storageFolder) {
        return StorageFolderResDto.builder()
            .folderId(storageFolder.getId())
            .folderName(storageFolder.getName())
            .build();
    }
}
