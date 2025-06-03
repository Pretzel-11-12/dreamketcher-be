package pretzel.dreamketcherbe.domain.notification.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pretzel.dreamketcherbe.domain.notification.dto.NotificationDto;
import pretzel.dreamketcherbe.domain.notification.service.SSEService;

@Component
@Slf4j
@RequiredArgsConstructor
public class EpisodeLikeNotificationEventHandler {

    private final SSEService SSEservice;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEpisodeLikeNotification(EpisodeLikeNotificationEvent event) {
        try {
            String message = String.format(
                "%s의 %s, %d화가 좋아요 %d개를 받았습니다.",
                event.getWebtoonTitle(),
                event.getEpisodeTitle(),
                event.getEpisodeNumber(),
                event.getCurrentLikeCount()
            );

            NotificationDto notificationDto = new NotificationDto(message, event.getCreatedAt());
            SSEservice.sendNotification(event.getMemberId(), notificationDto.message());

            log.info("좋아요 알림 전송 완료 - 작성자: {}, 에피소드: {}, 좋아요: {}",
                event.getMemberId(), event.getEpisodeId(), event.getCurrentLikeCount());
        } catch (Exception e) {
            log.error("좋아요 알림 전송 실패 - 작성자: {}, 에피소드: {}, 에러:{}",
                event.getMemberId(), event.getEpisodeId(), e.getMessage());
        }
    }
}
