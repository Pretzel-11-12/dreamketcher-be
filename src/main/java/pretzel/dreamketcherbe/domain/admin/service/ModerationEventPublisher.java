package pretzel.dreamketcherbe.domain.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.admin.dto.ModerationEventData;
import pretzel.dreamketcherbe.domain.admin.entity.ActionType;
import pretzel.dreamketcherbe.domain.admin.entity.TargetType;
import pretzel.dreamketcherbe.domain.report.entity.ReportReason;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishModerationLogEvent(
        Long adminId,
        ActionType actionType,
        TargetType targetType,
        Long targetId,
        ReportReason reason,
        String reasonText,
        String adminNote
    ) {
        ModerationEventData event = ModerationEventData.builder()
            .adminId(adminId)
            .actionType(actionType)
            .targetType(targetType)
            .targetId(targetId)
            .reason(reason)
            .reasonText(reasonText)
            .adminNote(adminNote)
            .build();

        log.debug("Publishing moderation event: {}", event);
        eventPublisher.publishEvent(event);
    }

}
