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
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.report.entity.EpisodeReport;

@Entity
@Table(name = "episode_report_notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EpisodeReportNotification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private EpisodeReport episodeReport;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @ManyToOne
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @Column(name = "webtoon_id", nullable = false)
    private Long webtoonId;

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
     * 신고자용 신고 접수 알림 생성
     */
    public static EpisodeReportNotification createForReporter(
        EpisodeReport episodeReport,
        Episode episode
    ) {
        return EpisodeReportNotification.builder()
            .reporterId(episodeReport.getReporterMemberId())
            .episodeReport(episodeReport)
            .episode(episode)
            .webtoonId(episode.getWebtoon().getId())
            .type(ReportNotificationType.REPORT_SUBMITTED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 피신고인용 신고 알림 생성
     */
    public static EpisodeReportNotification createForReported(
        EpisodeReport episodeReport,
        Episode episode
    ) {
        return EpisodeReportNotification.builder()
            .memberId(episode.getMember().getId())
            .episodeReport(episodeReport)
            .episode(episode)
            .webtoonId(episode.getWebtoon().getId())
            .type(ReportNotificationType.REPORT_RECEIVED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 신고 처리 반려 알림 - 신고자
     */
    public static EpisodeReportNotification createForReporterRejected(
        EpisodeReport episodeReport,
        Episode episode
    ) {
        return EpisodeReportNotification.builder()
            .reporterId(episodeReport.getReporterMemberId())
            .episodeReport(episodeReport)
            .episode(episode)
            .webtoonId(episode.getWebtoon().getId())
            .type(ReportNotificationType.REPORT_REJECTED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 신고 처리 반려 알림 - 피신고인
     */
    public static EpisodeReportNotification createForReportedRejected(
        EpisodeReport episodeReport,
        Episode episode
    ) {
        return EpisodeReportNotification.builder()
            .memberId(episode.getMember().getId())
            .episodeReport(episodeReport)
            .episode(episode)
            .webtoonId(episode.getWebtoon().getId())
            .type(ReportNotificationType.REPORT_REJECTED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 신고 처리 승인 알림 - 신고자
     */
    public static EpisodeReportNotification createForReporterApproved(
        EpisodeReport episodeReport,
        Episode episode
    ) {
        return EpisodeReportNotification.builder()
            .reporterId(episodeReport.getReporterMemberId())
            .episodeReport(episodeReport)
            .episode(episode)
            .webtoonId(episode.getWebtoon().getId())
            .type(ReportNotificationType.REPORT_APPROVED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

    /**
     * 신고 처리 승인 알림 - 피신고인
     */
    public static EpisodeReportNotification createForReportedApproved(
        EpisodeReport episodeReport,
        Episode episode
    ) {
        return EpisodeReportNotification.builder()
            .memberId(episode.getMember().getId())
            .episodeReport(episodeReport)
            .episode(episode)
            .webtoonId(episode.getWebtoon().getId())
            .type(ReportNotificationType.REPORT_APPROVED)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }
}
