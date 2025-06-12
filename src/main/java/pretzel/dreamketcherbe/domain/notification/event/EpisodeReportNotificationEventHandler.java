package pretzel.dreamketcherbe.domain.notification.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeException;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeExceptionType;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;
import pretzel.dreamketcherbe.domain.notification.dto.NotificationDto;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeReportNotification;
import pretzel.dreamketcherbe.domain.notification.repository.EpisodeReportNotificationRepository;
import pretzel.dreamketcherbe.domain.notification.service.SSEService;
import pretzel.dreamketcherbe.domain.report.entity.EpisodeReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;
import pretzel.dreamketcherbe.domain.report.repository.EpisodeReportRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class EpisodeReportNotificationEventHandler {

    private final SSEService SSEService;
    private final EpisodeReportNotificationRepository episodeReportNotificationRepository;
    private final EpisodeReportRepository episodeReportRepository;
    private final EpisodeRepository episodeRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEpisodeReportNotification(EpisodeReportNotificationEvent event) {
        try {
            EpisodeReport episodeReport = episodeReportRepository.findById(event.getReportId())
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다."));

            Episode episode = episodeRepository.findById(event.getEpisodeId())
                .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

            handleReportedNotification(event, episodeReport, episode);
            handleReportNotification(event, episodeReport, episode);

            log.info("신고 알림 전송 완료 - 신고자: {}, 피신고인: {}, 타입: {}",
                event.getReporterId(), event.getReportedMemberId(), event.getType());
        } catch (Exception e) {
            log.error("신고 알림 전송 실패 - 신고자: {}, 피신고인: {}, 타입: {}, 에러: {}",
                event.getReporterId(), event.getReportedMemberId(), event.getType(),
                e.getMessage());
        }
    }

    private void handleReportNotification(EpisodeReportNotificationEvent event,
        EpisodeReport episodeReport, Episode episode) {
        EpisodeReportNotification notification = createReporterNotification(event, episodeReport,
            episode);
        episodeReportNotificationRepository.save(notification);

        NotificationDto notificationDto = createEpisodeReporterNotification(event);
        SSEService.sendNotification(event.getReporterId(), notificationDto.message());
    }

    private void handleReportedNotification(EpisodeReportNotificationEvent event,
        EpisodeReport episodeReport, Episode episode) {
        EpisodeReportNotification notification = createReportedNotification(event, episodeReport,
            episode);
        episodeReportNotificationRepository.save(notification);

        NotificationDto notificationDto = createReportedEpisodeNotification(event);
        SSEService.sendNotification(event.getReportedMemberId(), notificationDto.message());
    }

    private NotificationDto createEpisodeReporterNotification(
        EpisodeReportNotificationEvent event) {
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
        EpisodeReportNotificationEvent event) {
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

    private EpisodeReportNotification createReporterNotification(
        EpisodeReportNotificationEvent event, EpisodeReport episodeReport, Episode episode) {
        return switch (event.getType()) {
            case PENDING -> EpisodeReportNotification.createForReporter(episodeReport, episode);
            case RESOLVED ->
                EpisodeReportNotification.createForReporterApproved(episodeReport, episode);
            case DISMISSED ->
                EpisodeReportNotification.createForReporterRejected(episodeReport, episode);
        };
    }

    private EpisodeReportNotification createReportedNotification(
        EpisodeReportNotificationEvent event, EpisodeReport episodeReport, Episode episode) {
        return switch (event.getType()) {
            case PENDING -> EpisodeReportNotification.createForReported(episodeReport, episode);
            case RESOLVED ->
                EpisodeReportNotification.createForReportedApproved(episodeReport, episode);
            case DISMISSED ->
                EpisodeReportNotification.createForReportedRejected(episodeReport, episode);
        };
    }

}
