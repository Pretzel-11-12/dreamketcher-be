package pretzel.dreamketcherbe.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;
import pretzel.dreamketcherbe.domain.report.entity.RecommentReport;

@Entity
@Table(name = "recomment_report_notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RecommentReportNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private RecommentReport recommentReport;

    @Column(name = "reporter_id")
    private Long reporterId;

    @Column(name = "webtoon_id", nullable = false)
    private long webtoonId;

    @Column(name = "episode_id", nullable = false)
    private long episodeId;

    @Column(name = "comment_id", nullable = false)
    private long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recomment_id", nullable = false)
    private Recomment recomment;

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
    public static RecommentReportNotification createForReporter(
        RecommentReport recommentReport, Recomment recomment
    ) {
        return RecommentReportNotification.builder()
            .reporterId(recommentReport.getReporterMemberId())
            .recommentReport(recommentReport)
            .webtoonId(recomment.getEpisode().getWebtoon().getId())
            .episodeId(recomment.getEpisode().getId())
            .commentId(recomment.getComment().getId())
            .recomment(recomment)
            .type(ReportNotificationType.REPORT_SUBMITTED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 피신고인용 신고 알림 생성
     */
    public static RecommentReportNotification createForReported(
        RecommentReport recommentReport, Recomment recomment
    ) {
        return RecommentReportNotification.builder()
            .memberId(recomment.getMember().getId())
            .recommentReport(recommentReport)
            .webtoonId(recomment.getEpisode().getWebtoon().getId())
            .episodeId(recomment.getEpisode().getId())
            .commentId(recomment.getComment().getId())
            .recomment(recomment)
            .type(ReportNotificationType.REPORT_RECEIVED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 신고 처리 반려 알림 - 신고자
     */
    public static RecommentReportNotification createForReporterRejected(
        RecommentReport recommentReport, Recomment recomment
    ) {
        return RecommentReportNotification.builder()
            .reporterId(recommentReport.getReporterMemberId())
            .recommentReport(recommentReport)
            .webtoonId(recomment.getEpisode().getWebtoon().getId())
            .episodeId(recomment.getEpisode().getId())
            .commentId(recomment.getComment().getId())
            .recomment(recomment)
            .type(ReportNotificationType.REPORT_REJECTED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 신고 처리 반려 알림 - 피신고인
     */
    public static RecommentReportNotification createForReportedRejected(
        RecommentReport recommentReport, Recomment recomment
    ) {
        return RecommentReportNotification.builder()
            .memberId(recomment.getMember().getId())
            .recommentReport(recommentReport)
            .webtoonId(recomment.getEpisode().getWebtoon().getId())
            .episodeId(recomment.getEpisode().getId())
            .commentId(recomment.getComment().getId())
            .recomment(recomment)
            .type(ReportNotificationType.REPORT_REJECTED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 신고 처리 승인 알림 - 신고자
     */
    public static RecommentReportNotification createForReporterApproved(
        RecommentReport recommentReport, Recomment recomment
    ) {
        return RecommentReportNotification.builder()
            .reporterId(recommentReport.getReporterMemberId())
            .recommentReport(recommentReport)
            .webtoonId(recomment.getEpisode().getWebtoon().getId())
            .episodeId(recomment.getEpisode().getId())
            .commentId(recomment.getComment().getId())
            .recomment(recomment)
            .type(ReportNotificationType.REPORT_APPROVED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 신고 처리 승인 알림 - 피신고인
     */
    public static RecommentReportNotification createForReportedApproved(
        RecommentReport recommentReport, Recomment recomment
    ) {
        return RecommentReportNotification.builder()
            .memberId(recomment.getMember().getId())
            .recommentReport(recommentReport)
            .webtoonId(recomment.getEpisode().getWebtoon().getId())
            .episodeId(recomment.getEpisode().getId())
            .commentId(recomment.getComment().getId())
            .recomment(recomment)
            .type(ReportNotificationType.REPORT_APPROVED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

}
