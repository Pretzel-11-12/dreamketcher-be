package pretzel.dreamketcherbe.domain.comment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentReqDto;
import pretzel.dreamketcherbe.domain.comment.exception.CommentException;
import pretzel.dreamketcherbe.domain.comment.exception.CommentExceptionType;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Table(name = "comments")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE comments SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "child_comment_count")
    private int childCommentCount;

    @Column(name = "recommendation_count", nullable = false)
    @ColumnDefault("0")
    private int recommendationCount;

    @Column(name = "not_recommendation_count", nullable = false)
    @ColumnDefault("0")
    private int notRecommendationCount;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "episode_id")
    private Episode episode;

    @ManyToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;

    @Builder
    public Comment(String content, int childCommentCount, int recommendationCount,
        int notRecommendationCount, Member member, Episode episode,
        Webtoon webtoon) {
        this.content = content;
        this.childCommentCount = childCommentCount;
        this.recommendationCount = recommendationCount;
        this.notRecommendationCount = notRecommendationCount;
        this.member = member;
        this.episode = episode;
        this.webtoon = webtoon;
    }

    public static Comment addOf(CreateCommentReqDto dto, Member member, Episode episode) {
        return Comment.builder()
            .content(dto.content())
            .member(member)
            .episode(episode)
            .webtoon(episode.getWebtoon())
            .build();
    }

    public void isAuthor(Long memberId) {
        if (!member.getId().equals(memberId)) {
            throw new CommentException(CommentExceptionType.UNAUTHORIZED_MEMBER);
        }
    }

    public void softDelete() {
        if (!this.isDeleted) {
            this.isDeleted = true;
        }
    }

    public void updateChildCommentCount(int count) {
        this.childCommentCount = count;
    }

    public void updateRecommendationCount(int count) {
        this.recommendationCount = count;
    }

    public void updateNotRecommendationCount(int count) {
        this.notRecommendationCount = count;
    }
}
