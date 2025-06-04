package pretzel.dreamketcherbe.domain.notification.event;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;
import pretzel.dreamketcherbe.domain.comment.exception.CommentException;
import pretzel.dreamketcherbe.domain.comment.exception.CommentExceptionType;
import pretzel.dreamketcherbe.domain.comment.repository.CommentRepository;
import pretzel.dreamketcherbe.domain.notification.dto.NotificationDto;
import pretzel.dreamketcherbe.domain.notification.entity.CommentReportNotification;
import pretzel.dreamketcherbe.domain.notification.repository.CommentReportNotificationRepository;
import pretzel.dreamketcherbe.domain.notification.service.SSEService;
import pretzel.dreamketcherbe.domain.report.entity.CommentReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;
import pretzel.dreamketcherbe.domain.report.repository.CommentReportRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentReportNotificationEventHandler {

    private final SSEService SSEService;
    private final CommentReportRepository commentReportRepository;
    private final CommentRepository commentRepository;
    private final CommentReportNotificationRepository commentReportNotificationRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentReportNotification(CommentReportNotificationEvent event) {
        try {

            CommentReport commentReport = commentReportRepository.findById(event.getReportId())
                .orElseThrow(() -> new RuntimeException("신고를 찾을 수 없습니다."));

            Comment comment = commentRepository.findById(event.getCommentId())
                .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

            List<CommentReportNotification> notifications = new ArrayList<>();

            switch (event.getType()) {
                case PENDING -> {
                    notifications.add(
                        CommentReportNotification.createForReporter(commentReport, comment));
                    notifications.add(
                        CommentReportNotification.createForReported(commentReport, comment));
                }
                case RESOLVED -> {
                    notifications.add(
                        CommentReportNotification.createForReporterApproved(commentReport,
                            comment));
                    notifications.add(
                        CommentReportNotification.createForReportedApproved(commentReport,
                            comment));
                }
                case DISMISSED -> {
                    notifications.add(
                        CommentReportNotification.createForReporterRejected(commentReport,
                            comment));
                    notifications.add(
                        CommentReportNotification.createForReportedRejected(commentReport,
                            comment));
                }
            }

            commentReportNotificationRepository.saveAll(notifications);

            for (CommentReportNotification notification : notifications) {
                Long targetMemberId =
                    notification.getMemberId() != null ? notification.getMemberId()
                        : notification.getReporterId();

                String message = "";

                switch (notification.getType()) {
                    case REPORT_SUBMITTED, REPORT_APPROVED, REPORT_REJECTED -> {
                        NotificationDto notificationDto = createReportCommentNotification(event);
                        message = notificationDto.message();
                    }
                    case REPORT_RECEIVED -> {
                        NotificationDto notificationDto = createReportedCommentNotification(event);
                        message = notificationDto.message();
                    }
                    default -> log.warn("알 수 없는 신고 알림 타입: {}", notification.getType());
                }
                SSEService.sendNotification(targetMemberId, message);
            }

            log.info("신고 알림 전송 완료 - 신고자: {}, 피신고인: {}, 타입: {}",
                event.getReporterId(), event.getReportedMemberId(), event.getType());
        } catch (Exception e) {
            log.error("신고 알림 전송 실패 - 신고자: {}, 피신고인: {}, 타입: {}, 에러: {}",
                event.getReporterId(), event.getReportedMemberId(), event.getType(),
                e.getMessage());
        }
    }

    private NotificationDto createReportedCommentNotification(
        CommentReportNotificationEvent event) {
        String message;

        if (event.getType() == ReportStatus.PENDING) {
            message = String.format("%s의 %s, %d화 댓글이 신고되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber());
        } else if (event.getType() == ReportStatus.RESOLVED) {
            message = String.format("%s의 %s, %d화 댓글의 신고가 승인되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber());
        } else {
            message = String.format("%s의 %s, %d화 댓글의 신고가 거절되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber());
        }

        return new NotificationDto(message, event.getCreatedAt());
    }

    private NotificationDto createReportCommentNotification(CommentReportNotificationEvent event) {
        String message;

        if (event.getType() == ReportStatus.PENDING) {
            message = String.format("%s의 %s, %d화 댓글에 대한 신고기 제출되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber());
        } else if (event.getType() == ReportStatus.RESOLVED) {
            message = String.format("%s의 %s, %d화 댓글의 신고가 승인되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber());
        } else {
            message = String.format("%s의 %s, %d화 댓글의 신고가 거절되었습니다.", event.getWebtoonTitle(),
                event.getEpisodeTitle(), event.getEpisodeNumber());
        }

        return new NotificationDto(message, event.getCreatedAt());
    }
}
