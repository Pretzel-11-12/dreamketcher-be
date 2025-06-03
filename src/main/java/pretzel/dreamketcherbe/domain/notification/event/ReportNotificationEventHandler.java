package pretzel.dreamketcherbe.domain.notification.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pretzel.dreamketcherbe.domain.notification.dto.NotificationDto;
import pretzel.dreamketcherbe.domain.notification.service.SSEService;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportNotificationEventHandler {

    private final SSEService SSEService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEpisodeReportNotification(ReportNotificationEvent event) {
        try {
            // 피신고인 에게 알림 전송
            NotificationDto notificationDto = createReportedEpisodeNotification(event);
            SSEService.sendNotification(event.getReportedMemberId(),
                notificationDto.message());

            // 신고자에게 알림 전송
            NotificationDto reportedNotificationDto = createEpisodeReporterNotification(
                event);
            SSEService.sendNotification(event.getReporterId(),
                String.valueOf(reportedNotificationDto));

            log.info("신고 알림 전송 완료 - 신고자: {}, 피신고인: {}, 타입: {}",
                event.getReporterId(), event.getReportedMemberId(), event.getType());
        } catch (Exception e) {
            log.error("신고 알림 전송 실패 - 신고자: {}, 피신고인: {}, 타입: {}, 에러: {}",
                event.getReporterId(), event.getReportedMemberId(), event.getType(),
                e.getMessage());
        }
    }

    private NotificationDto createEpisodeReporterNotification(
        ReportNotificationEvent event) {
        String message;

        if (event.getType() == ReportStatus.PENDING) {
            message = String.format("%s의 %s, %s에 신고가 제출되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber());
        } else if (event.getType() == ReportStatus.RESOLVED) {
            message = String.format("%s의 %s, %s에 대한 신고가 승인되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber());
        } else {
            message = String.format("%s의 %s, %s에 대한 신고가 거절되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber());
        }

        return new NotificationDto(message, event.getCreatedAt());
    }

    private NotificationDto createReportedEpisodeNotification(
        ReportNotificationEvent event) {
        String message;

        if (event.getType() == ReportStatus.PENDING) {
            message = String.format("%s의 %s, %s에 대한 신고가 접수되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber());
        } else if (event.getType() == ReportStatus.RESOLVED) {
            message = String.format("%s의 %s, %s에 대한 신고가 승인되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber());
        } else {
            message = String.format("%s의 %s, %s에 대한 신고가 거절되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber());
        }

        return new NotificationDto(message, event.getCreatedAt());
    }

}
