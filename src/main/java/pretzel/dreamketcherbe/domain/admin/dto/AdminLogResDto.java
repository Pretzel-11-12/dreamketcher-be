package pretzel.dreamketcherbe.domain.admin.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record AdminLogResDto(
    List<AdminLogItemDto> logs,
    long total,
    int page,
    int size
) {

}
