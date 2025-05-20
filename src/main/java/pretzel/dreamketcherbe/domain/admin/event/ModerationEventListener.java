package pretzel.dreamketcherbe.domain.admin.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.admin.dto.ModerationEventData;
import pretzel.dreamketcherbe.domain.admin.entity.ModerationLog;
import pretzel.dreamketcherbe.domain.admin.repository.ModerationLogRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationEventListener {

    private final ModerationLogRepository moderationLogRepository;

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleModerationEvent(ModerationEventData event) {
        log.info("관리자 액션 로깅 시작: {} - adminId={}, targetType={}, targetId={}", 
            event.actionType(), event.adminId(), event.targetType(), event.targetId());

        try {
            ModerationLog moderationLog = ModerationLog.builder()
                .adminId(event.adminId())
                .actionType(event.actionType())
                .targetType(event.targetType())
                .targetId(event.targetId())
                .reason(event.reason())
                .reasonText(event.reasonText())
                .adminNote(event.adminNote())
                .build();

            ModerationLog saved = moderationLogRepository.save(moderationLog);
            log.info("관리자 액션 로깅 완료: id={}, actionType={}, targetId={}", 
                saved.getId(), saved.getActionType(), saved.getTargetId());
        } catch (Exception e) {
            log.error("관리자 액션 로깅 실패: {} - {}", event.actionType(), e.getMessage(), e);
        }
    }
}
