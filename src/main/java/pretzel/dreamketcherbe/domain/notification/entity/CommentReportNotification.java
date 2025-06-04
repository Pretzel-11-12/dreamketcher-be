package pretzel.dreamketcherbe.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;
import pretzel.dreamketcherbe.domain.report.entity.CommentReport;

@Entity
@Table(name = "comment_report_notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CommentReportNotification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private CommentReport commentReport;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(name = "webtoon_id", nullable = false)
    private long webtoonId;

    @Column(name = "episode_id", nullable = false)
    private long episodeId;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(name = "notification_type", nullable = false)
    private ReportNotificationType type;

    @Column(name = "is_read", nullable = false)
    @ColumnDefault("false")
    private Boolean isRead;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * 신고자용 신고 알림 접수 생성
     */
    public static CommentReportNotification createForReporter(
        CommentReport commentReport, Comment comment
    ) {
        return CommentReportNotification.builder()
            .reporterId(commentReport.getReporterMemberId())
            .commentReport(commentReport)
            .webtoonId(comment.getEpisode().getWebtoon().getId())
            .episodeId(comment.getEpisode().getId())
            .comment(comment)
            .type(ReportNotificationType.REPORT_SUBMITTED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 피신고인용 신고 알림 생성
     */
    public static CommentReportNotification createForReported(
        CommentReport commentReport, Comment comment
    ) {
        return CommentReportNotification.builder()
            .memberId(comment.getMember().getId())
            .commentReport(commentReport)
            .webtoonId(comment.getEpisode().getWebtoon().getId())
            .episodeId(comment.getEpisode().getId())
            .comment(comment)
            .type(ReportNotificationType.REPORT_RECEIVED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 신고 처리 반려 알림 - 신고자
     */
    public static CommentReportNotification createForReporterRejected(
        CommentReport commentReport, Comment comment
    ) {
        return CommentReportNotification.builder()
            .reporterId(commentReport.getReporterMemberId())
            .commentReport(commentReport)
            .webtoonId(comment.getEpisode().getWebtoon().getId())
            .episodeId(comment.getEpisode().getId())
            .comment(comment)
            .type(ReportNotificationType.REPORT_REJECTED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 신고 처리 반려 알림 - 피신고인
     */
    public static CommentReportNotification createForReportedCompleted(
        CommentReport commentReport, Comment comment
    ) {
        return CommentReportNotification.builder()
            .memberId(comment.getMember().getId())
            .commentReport(commentReport)
            .webtoonId(comment.getEpisode().getWebtoon().getId())
            .episodeId(comment.getEpisode().getId())
            .comment(comment)
            .type(ReportNotificationType.REPORT_REJECTED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 신고 처리 승인 알림 - 신고자
     */
    public static CommentReportNotification createForReporterCompleted(
        CommentReport commentReport, Comment comment
    ) {
        return CommentReportNotification.builder()
            .reporterId(commentReport.getReporterMemberId())
            .commentReport(commentReport)
            .webtoonId(comment.getEpisode().getWebtoon().getId())
            .episodeId(comment.getEpisode().getId())
            .comment(comment)
            .type(ReportNotificationType.REPORT_APPROVED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 신고 처리 승인 알림 - 피신고인
     */
    public static CommentReportNotification createForReportedApproved(
        CommentReport commentReport, Comment comment
    ) {
        return CommentReportNotification.builder()
            .memberId(comment.getMember().getId())
            .commentReport(commentReport)
            .webtoonId(comment.getEpisode().getWebtoon().getId())
            .episodeId(comment.getEpisode().getId())
            .comment(comment)
            .type(ReportNotificationType.REPORT_APPROVED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }
}
