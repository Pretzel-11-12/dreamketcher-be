package pretzel.dreamketcherbe.domain.notification.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pretzel.dreamketcherbe.domain.notification.dto.NotificationDto;
import pretzel.dreamketcherbe.domain.notification.entity.RecommentNotificationType;
import pretzel.dreamketcherbe.domain.notification.service.SSEService;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecommentRecOrNotRecNotificationEventHandler {

    private final SSEService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecommentRecOrNotRecNotificationEvent(
        RecommentRecOrNotRecNotificationEvent event) {
        try {
            String message = createNotificationMessage(event);
            NotificationDto notificationDto = new NotificationDto(message, event.getCreatedAt());
            sseService.sendNotification(event.getMemberId(), notificationDto.message());

            log.info("대댓글 {} 알림 전송 완료 - 작성자: {}, 대댓글: {}, 개수: {}",
                event.getType() == RecommentNotificationType.RECOMMENDATION ? "추천" : "비추천",
                event.getMemberId(), event.getRecommentId(), event.getCount());
        } catch (Exception e) {
            log.error("대댓글 {} 알림 전송 실패 - 작성자: {}, 대댓글: {}, 개수: {}",
                event.getType() == RecommentNotificationType.RECOMMENDATION ? "추천" : "비추천",
                event.getMemberId(), event.getRecommentId(), event.getCount(), e);
        }
    }

    private String createNotificationMessage(RecommentRecOrNotRecNotificationEvent event) {
        String action = event.getType() == RecommentNotificationType.RECOMMENDATION ? "추천" : "비추천";

        return String.format(
            "%s의 %s, %d화에 작성한 대댓글이 %s %d개를 받았습니다!",
            event.getWebtoonTitle(),
            event.getEpisodeTitle(),
            event.getEpisodeNumber(),
            action,
            event.getCount()
        );
    }
}
