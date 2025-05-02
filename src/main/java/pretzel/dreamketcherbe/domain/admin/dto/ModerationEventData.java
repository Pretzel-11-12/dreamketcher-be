package pretzel.dreamketcherbe.domain.admin.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.admin.entity.ActionType;
import pretzel.dreamketcherbe.domain.admin.entity.TargetType;
import pretzel.dreamketcherbe.domain.report.entity.ReportReason;

@Builder
public record ModerationEventData(
    Long adminId,
    ActionType actionType,
    TargetType targetType,
    Long targetId,
    ReportReason reason,
    String reasonText,
    String adminNote
) {

}
