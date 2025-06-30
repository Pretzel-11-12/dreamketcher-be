package pretzel.dreamketcherbe.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;

@Entity
@Table(name = "recomment_rec_notrec_notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RecommentRecOrNotRecNotification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "recomment_id", nullable = false)
    private Recomment recomment;

    @Column(name = "episode_id", nullable = false)
    private Long episodeId;

    @Column(name = "webtoon_id", nullable = false)
    private Long webtoonId;

    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private RecommentNotificationType type;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "is_read", nullable = false)
    @ColumnDefault("false")
    private Boolean isRead;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    /**
     * 추천 알림 생성
     */
    public static RecommentRecOrNotRecNotification createForRecommendation(
        Recomment recomment, int recommendationCount
    ) {
        return RecommentRecOrNotRecNotification.builder()
            .memberId(recomment.getMember().getId())
            .recomment(recomment)
            .episodeId(recomment.getComment().getEpisode().getId())
            .webtoonId(recomment.getComment().getEpisode().getWebtoon().getId())
            .commentId(recomment.getComment().getId())
            .type(RecommentNotificationType.RECOMMENDATION)
            .count(recommendationCount)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(7))
            .build();
    }

    /**
     * 비추천 알림 생성
     */
    public static RecommentRecOrNotRecNotification createForNotRecommendation(
        Recomment recomment, int notRecommendationCount
    ) {
        return RecommentRecOrNotRecNotification.builder()
            .memberId(recomment.getMember().getId())
            .recomment(recomment)
            .episodeId(recomment.getComment().getEpisode().getId())
            .webtoonId(recomment.getComment().getEpisode().getWebtoon().getId())
            .commentId(recomment.getComment().getId())
            .type(RecommentNotificationType.NOT_RECOMMENDATION)
            .count(notRecommendationCount)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(7))
            .build();
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

}
