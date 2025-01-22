package pretzel.dreamketcherbe.domain.comment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentReqDto;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Table(name = "re_comments")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE re_comments SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Recomment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "parent_comment_id", nullable = false)
    private Long parentCommentId;

    @Column(name = "comment_order")
    private int commentOrder;

    @Column(name = "recommendation_count", nullable = false)
    @ColumnDefault("0")
    @Setter
    private int recommendationCount;

    @Column(name = "not_recommendation_count", nullable = false)
    @ColumnDefault("0")
    @Setter
    private int notRecommendationCount;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    private Episode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Builder
    public Recomment(String content, Long parentCommentId, int commentOrder,
        int recommendationCount, int notRecommendationCount, Member member,
        Episode episode, Webtoon webtoon, Comment comment) {
        this.content = content;
        this.parentCommentId = parentCommentId;
        this.commentOrder = commentOrder;
        this.recommendationCount = recommendationCount;
        this.notRecommendationCount = notRecommendationCount;
        this.member = member;
        this.webtoon = webtoon;
        this.episode = episode;
        this.comment = comment;
    }

    public static Recomment addOf(CreateRecommentReqDto dto, int commentOrder, Member member,
        Episode episode, Comment comment) {
        return Recomment.builder()
            .content(dto.content())
            .parentCommentId(comment.getId())
            .commentOrder(commentOrder)
            .member(member)
            .webtoon(episode.getWebtoon())
            .episode(episode)
            .comment(comment)
            .build();
    }

    public void isAuthor(Long memberId) {
        if (!member.getId().equals(memberId)) {
            throw new IllegalStateException(memberId + ", 답글 작성자가 아닙니다.");
        }
    }

    public void softDelete() {
        if (!isDeleted) {
            this.isDeleted = true;
        }
    }
}
