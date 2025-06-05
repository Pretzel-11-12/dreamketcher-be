package pretzel.dreamketcherbe.domain.notification.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;
import pretzel.dreamketcherbe.domain.notification.dto.NotificationDto;
import pretzel.dreamketcherbe.domain.notification.entity.CommentNotificationType;
import pretzel.dreamketcherbe.domain.notification.entity.CommentRecOrNotRecNotification;
import pretzel.dreamketcherbe.domain.notification.service.SSEService;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentRecOrNotRecNotificationEventHandler {

    private final SSEService SSEService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentRecOrNotRecNotificationEvent(
        CommentRecOrNotRecNotificationEvent event) {
        try {
            String message = createNotificationMessage(event);
            NotificationDto notificationDto = new NotificationDto(message, event.getCreatedAt());
            SSEService.sendNotification(event.getMemberId(), notificationDto.message());

            log.info("댓글 {} 알림 전송 완료 - 작성자: {}, 댓글: {}, 개수: {}",
                event.getType() == CommentNotificationType.RECOMMENDATION ? "추천" : "비추천",
                event.getMemberId(), event.getCommentId(), event.getCount());
        } catch (Exception e) {
            log.error("댓글 {} 알림 전송 실패 - 작성자: {}, 댓글: {}, 개수: {}",
                event.getType() == CommentNotificationType.RECOMMENDATION ? "추천" : "비추천",
                event.getMemberId(), event.getCommentId(), event.getCount(), e);
        }
    }

    private String createNotificationMessage(CommentRecOrNotRecNotificationEvent event) {
        String action = event.getType() == CommentNotificationType.RECOMMENDATION ? "추천" : "비추천";

        return String.format(
            "%s의 %s, %d화에 작성한 댓글이 %s %d개를 받았습니다!",
            event.getWebtoonTitle(),
            event.getEpisodeTitle(),
            event.getEpisodeNumber(),
            action,
            event.getCount()
        );
    }
}
