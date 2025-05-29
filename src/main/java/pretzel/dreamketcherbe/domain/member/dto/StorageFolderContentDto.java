package pretzel.dreamketcherbe.domain.member.dto;

import java.util.List;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;

@Builder
public record StorageFolderContentDto(
    Long folderId,
    String folderName,
    List<StorageItemContentDto> items
) {

    public static StorageFolderContentDto of(StorageFolder folder, List<StorageItemContentDto> items) {
        return StorageFolderContentDto.builder()
            .folderId(folder.getId())
            .folderName(folder.getName())
            .items(items)
            .build();
    }
}
