package pretzel.dreamketcherbe.domain.member.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record StorageFolderResDto(
    List<StorageFolderContentDto> folders,
    long total
) {
    public static StorageFolderResDto of(List<StorageFolderContentDto> content, long total) {
        return StorageFolderResDto.builder()
            .folders(content)
            .total(total)
            .build();
    }
}
