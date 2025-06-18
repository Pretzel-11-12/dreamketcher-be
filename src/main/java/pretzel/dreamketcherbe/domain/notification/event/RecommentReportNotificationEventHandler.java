package pretzel.dreamketcherbe.domain.notification.event;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;
import pretzel.dreamketcherbe.domain.comment.exception.CommentException;
import pretzel.dreamketcherbe.domain.comment.exception.CommentExceptionType;
import pretzel.dreamketcherbe.domain.comment.repository.RecommentRepository;
import pretzel.dreamketcherbe.domain.notification.dto.NotificationDto;
import pretzel.dreamketcherbe.domain.notification.entity.RecommentReportNotification;
import pretzel.dreamketcherbe.domain.notification.repository.RecommentReportNotificationRepository;
import pretzel.dreamketcherbe.domain.notification.service.SSEService;
import pretzel.dreamketcherbe.domain.report.entity.RecommentReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;
import pretzel.dreamketcherbe.domain.report.repository.RecommentReportRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecommentReportNotificationEventHandler {

    private final SSEService sseService;
    private final RecommentReportRepository recommentReportRepository;
    private final RecommentRepository recommentRepository;
    private final RecommentReportNotificationRepository recommentReportNotificationRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecommentReportNotification(RecommentReportNotificationEvent event) {
        try {

            RecommentReport recommentReport = recommentReportRepository.findById(
                    event.getReportId())
                .orElseThrow(() -> new RuntimeException("신고를 찾을 수 없습니다."));

            Recomment recomment = recommentRepository.findById(event.getRecommentId())
                .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

            List<RecommentReportNotification> notifications = new ArrayList<>();

            switch (event.getType()) {
                case PENDING -> {
                    notifications.add(
                        RecommentReportNotification.createForReporter(recommentReport, recomment));
                    notifications.add(
                        RecommentReportNotification.createForReported(recommentReport, recomment));
                }
                case RESOLVED -> {
                    notifications.add(
                        RecommentReportNotification.createForReporterApproved(recommentReport,
                            recomment));
                    notifications.add(
                        RecommentReportNotification.createForReportedApproved(recommentReport,
                            recomment));
                }
                case DISMISSED -> {
                    notifications.add(
                        RecommentReportNotification.createForReporterRejected(recommentReport,
                            recomment));
                    notifications.add(
                        RecommentReportNotification.createForReportedRejected(recommentReport,
                            recomment));
                }
            }

            recommentReportNotificationRepository.saveAll(notifications);

            for (RecommentReportNotification notification : notifications) {
                Long targetMemberId =
                    notification.getMemberId() != null ? notification.getMemberId()
                        : notification.getReporterId();

                String message = "";

                switch (notification.getType()) {
                    case REPORT_SUBMITTED, REPORT_APPROVED, REPORT_REJECTED -> {
                        NotificationDto notificationDto = createReportRecommentNotification(event);
                        message = notificationDto.message();
                    }
                    case REPORT_RECEIVED -> {
                        NotificationDto notificationDto = createReportedRecommentNotification(
                            event);
                        message = notificationDto.message();
                    }
                    default -> log.warn("알 수 없는 신고 알림 타입: {}", notification.getType());
                }
                sseService.sendNotification(targetMemberId, message);
            }

            log.info("답글 신고 알림 전송 완료 - 신고자: {}, 피신고인: {}, 타입: {}",
                event.getReporterId(), event.getReportedMemberId(), event.getType());
        } catch (Exception e) {
            log.error("답글 신고 알림 전송 실패 - 신고자: {}, 피신고인: {}, 타입: {}, 에러: {}",
                event.getReporterId(), event.getReportedMemberId(), event.getType(),
                e.getMessage());
        }
    }

    private NotificationDto createReportedRecommentNotification(
        RecommentReportNotificationEvent event) {
        String message;

        if (event.getType() == ReportStatus.PENDING) {
            message = String.format("%s의 %s, %d화 댓글 %s의 답글이 신고되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber(), event.getCommentContent());
        } else if (event.getType() == ReportStatus.RESOLVED) {
            message = String.format("%s의 %s, %d화 댓글 %s의 답글의 신고가 승인되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber(), event.getCommentContent());
        } else {
            message = String.format("%s의 %s, %d화 댓글 %s의 답글의 신고가 거절되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber(), event.getCommentContent());
        }

        return new NotificationDto(message, event.getCreatedAt());
    }

    private NotificationDto createReportRecommentNotification(
        RecommentReportNotificationEvent event) {
        String message;

        if (event.getType() == ReportStatus.PENDING) {
            message = String.format("%s의 %s, %d화 댓글 %s의 답글에 대한 신고가 제출되었습니다.",
                event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber(), event.getCommentContent());
        } else if (event.getType() == ReportStatus.RESOLVED) {
            message = String.format("%s의 %s, %d화 댓글 %s의 답글의 신고가 승인되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber(), event.getCommentContent());
        } else {
            message = String.format("%s의 %s, %d화 댓글 %s의 답글의 신고가 거절되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber(), event.getCommentContent());
        }

        return new NotificationDto(message, event.getCreatedAt());
    }

}
