package pretzel.dreamketcherbe.domain.member.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record StorageItemResDto(
    Long folderId,
    String folderName,
    List<StorageItemDetailContentDto> content,
    long total
) {
    public static StorageItemResDto of(
        Long folderId,
        String folderName,
        List<StorageItemDetailContentDto> content,
        long total
    ) {
        return StorageItemResDto.builder()
            .folderId(folderId)
            .folderName(folderName)
            .content(content)
            .total(total)
            .build();
    }
}
