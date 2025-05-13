package pretzel.dreamketcherbe.domain.member.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;

@Builder
public record CreateFolderResDto(
    Long id,
    String folderName
) {
    public static CreateFolderResDto of(StorageFolder storageFolder) {
        return CreateFolderResDto.builder()
            .id(storageFolder.getId())
            .folderName(storageFolder.getName())
            .build();
    }
}
