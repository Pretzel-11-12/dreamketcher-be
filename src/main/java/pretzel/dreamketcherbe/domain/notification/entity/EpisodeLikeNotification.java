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

@Entity
@Table(name = "episode_like_notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EpisodeLikeNotification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long authorId;

    @ManyToOne
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @Column(name = "webtoon_id", nullable = false)
    private Long webtoonId;

    @Column(name = "episode_like_count", nullable = false)
    private int likeCount;

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
     * 알림 생성
     */
    public static EpisodeLikeNotification createForAuthor(Episode episode, int likeCount) {
        return EpisodeLikeNotification.builder()
            .authorId(episode.getMember().getId())
            .episode(episode)
            .webtoonId(episode.getWebtoon().getId())
            .likeCount(likeCount)
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();
    }

}
