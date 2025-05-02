package pretzel.dreamketcherbe.domain.comment.entity;

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

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
@SQLDelete(sql = "UPDATE comments SET status = 'DELETED' WHERE id = ?")
@SQLRestriction("status = 'NORMAL'")
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'NORMAL'")
    private CommentStatus status;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "episode_id")
    private Episode episode;

    @ManyToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;

    /**
     * 새로운 댓글 엔티티를 생성합니다.
     *
     * 댓글의 내용, 자식 댓글 수, 추천/비추천 수, 작성자, 연결된 에피소드 및 웹툰 정보를 초기화하며,
     * 상태는 기본적으로 NORMAL로 설정됩니다.
     */
    @Builder
    public Comment(String content, int childCommentCount, int recommendationCount,
        int notRecommendationCount, Member member, Episode episode, Webtoon webtoon) {
        this.content = content;
        this.childCommentCount = childCommentCount;
        this.recommendationCount = recommendationCount;
        this.notRecommendationCount = notRecommendationCount;
        this.member = member;
        this.episode = episode;
        this.webtoon = webtoon;
        this.status = CommentStatus.NORMAL;
    }

    /**
     * 주어진 DTO, 회원, 에피소드를 기반으로 새로운 댓글 엔티티를 생성합니다.
     *
     * @param dto 댓글 생성 요청 데이터
     * @param member 댓글 작성자
     * @param episode 댓글이 속한 에피소드
     * @return 생성된 댓글 엔티티
     */
    public static Comment addOf(CreateCommentReqDto dto, Member member, Episode episode) {
        return Comment.builder()
            .content(dto.content())
            .member(member)
            .episode(episode)
            .webtoon(episode.getWebtoon())
            .build();
    }

    /**
     * 주어진 회원 ID가 댓글 작성자인지 확인합니다.
     *
     * 작성자가 아닐 경우 UNAUTHORIZED_MEMBER 예외를 발생시킵니다.
     *
     * @param memberId 확인할 회원의 ID
     * @throws CommentException 작성자가 아닌 경우 발생
     */
    public void isAuthor(Long memberId) {
        if (!member.getId().equals(memberId)) {
            throw new CommentException(CommentExceptionType.UNAUTHORIZED_MEMBER);
        }
    }

    /**
     * 댓글의 상태를 삭제됨(DELETED)으로 변경합니다.
     */
    public void softDelete() {
        this.status = CommentStatus.DELETED;
    }

    /**
     * 댓글의 상태가 NORMAL일 때 REPORTED로 변경합니다.
     *
     * 댓글이 이미 신고되었거나 삭제된 경우에는 상태가 변경되지 않습니다.
     */
    public void report() {
        if (this.status == CommentStatus.NORMAL) {
            this.status = CommentStatus.REPORTED;
        }
    }

    public void updateChildCommentCount(int count) {
        this.childCommentCount = count;
    }

    public void updateRecommendationCount(int count) {
        this.recommendationCount = count;
    }

    /**
     * 비추천(싫어요) 수를 지정한 값으로 업데이트합니다.
     *
     * @param count 새로운 비추천(싫어요) 수
     */
    public void updateNotRecommendationCount(int count) {
        this.notRecommendationCount = count;
    }

    /**
     * 댓글이 삭제 상태인지 여부를 반환합니다.
     *
     * @return 댓글이 삭제(Deleted) 상태이면 true, 아니면 false
     */
    public boolean isDeleted() {
        return this.status == CommentStatus.DELETED;
    }

    /**
     * 댓글이 신고된 상태인지 여부를 반환합니다.
     *
     * @return 댓글의 상태가 REPORTED이면 true, 그렇지 않으면 false
     */
    public boolean isReported() {
        return this.status == CommentStatus.REPORTED;
    }
}
