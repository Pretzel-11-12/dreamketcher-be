package pretzel.dreamketcherbe.domain.admin.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.admin.entity.ActionType;
import pretzel.dreamketcherbe.domain.admin.entity.TargetType;

@Builder
public record AdminLogItemDto(
    LocalDateTime time,
    Long adminId,
    ActionType actionType,
    TargetType targetType,
    Long targetId,
    String reasonText,
    String adminNote
) {

}
