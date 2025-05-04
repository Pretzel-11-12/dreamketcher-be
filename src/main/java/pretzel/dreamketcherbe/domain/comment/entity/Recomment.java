package pretzel.dreamketcherbe.domain.comment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentReqDto;
import pretzel.dreamketcherbe.domain.comment.exception.CommentException;
import pretzel.dreamketcherbe.domain.comment.exception.CommentExceptionType;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Table(name = "re_comments")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE re_comments SET status = 'DELETED' WHERE id = ?")
@SQLRestriction("status = 'NORMAL'")
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
    private int recommendationCount;

    @Column(name = "not_recommendation_count", nullable = false)
    @ColumnDefault("0")
    private int notRecommendationCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'NORMAL'")
    private RecommentStatus status;

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
        this.status = RecommentStatus.NORMAL;
    }

    public static Recomment addOf(String content, int commentOrder, Member member,
        Episode episode, Comment comment) {
        return Recomment.builder()
            .content(content)
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
            throw new CommentException(CommentExceptionType.UNAUTHORIZED_MEMBER);
        }
    }

    public void softDelete() {
        this.status = RecommentStatus.DELETED;
    }

    public void report() {
        if (this.status == RecommentStatus.NORMAL) {
            this.status = RecommentStatus.REPORTED;
        }
    }

    public void updateRecommendationCount(int count) {
        this.recommendationCount = count;
    }

    public void updateNotRecommendationCount(int count) {
        this.notRecommendationCount = count;
    }

    public boolean isDeleted() {
        return this.status == RecommentStatus.DELETED;
    }

    public boolean isReported() {
        return this.status == RecommentStatus.REPORTED;
    }
}
