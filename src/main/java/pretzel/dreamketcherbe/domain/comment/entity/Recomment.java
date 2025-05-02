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

    /**
     * 새로운 대댓글 엔티티를 생성하고 초기 상태를 NORMAL로 설정합니다.
     *
     * @param content 대댓글의 내용
     * @param parentCommentId 부모 댓글의 ID
     * @param commentOrder 대댓글의 정렬 순서
     * @param recommendationCount 추천 수
     * @param notRecommendationCount 비추천 수
     * @param member 작성자 회원 엔티티
     * @param episode 관련 에피소드 엔티티
     * @param webtoon 관련 웹툰 엔티티
     * @param comment 부모 댓글 엔티티
     */
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

    /**
     * CreateRecommentReqDto와 관련 엔티티 정보를 기반으로 새로운 대댓글(Recomment) 인스턴스를 생성합니다.
     *
     * @param dto 대댓글 생성 요청 데이터 전송 객체
     * @param commentOrder 대댓글의 정렬 순서
     * @param member 대댓글 작성자
     * @param episode 대댓글이 속한 에피소드
     * @param comment 부모 댓글
     * @return 생성된 Recomment 엔티티
     */
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

    /**
     * 주어진 회원 ID가 이 대댓글의 작성자인지 검증하며, 작성자가 아닐 경우 예외를 발생시킵니다.
     *
     * @param memberId 검증할 회원의 ID
     * @throws CommentException 작성자가 아닌 경우 UNAUTHORIZED_MEMBER 예외를 발생시킵니다.
     */
    public void isAuthor(Long memberId) {
        if (!member.getId().equals(memberId)) {
            throw new CommentException(CommentExceptionType.UNAUTHORIZED_MEMBER);
        }
    }

    /**
     * 이 대댓글의 상태를 삭제됨(DELETED)으로 변경합니다.
     */
    public void softDelete() {
        this.status = RecommentStatus.DELETED;
    }

    /**
     * 댓글의 상태를 신고됨(REPORTED)으로 변경합니다.
     * 현재 상태가 NORMAL인 경우에만 상태가 변경됩니다.
     */
    public void report() {
        if (this.status == RecommentStatus.NORMAL) {
            this.status = RecommentStatus.REPORTED;
        }
    }

    public void updateRecommendationCount(int count) {
        this.recommendationCount = count;
    }

    /**
     * 비추천(반대) 수를 지정한 값으로 업데이트합니다.
     *
     * @param count 새로운 비추천(반대) 수
     */
    public void updateNotRecommendationCount(int count) {
        this.notRecommendationCount = count;
    }

    /**
     * 현재 대댓글의 상태가 삭제됨(Deleted)인지 여부를 반환합니다.
     *
     * @return 삭제된 상태이면 true, 아니면 false
     */
    public boolean isDeleted() {
        return this.status == RecommentStatus.DELETED;
    }

    /**
     * 이 대댓글의 상태가 신고됨(REPORTED)인지 여부를 반환합니다.
     *
     * @return 상태가 REPORTED이면 true, 그렇지 않으면 false
     */
    public boolean isReported() {
        return this.status == RecommentStatus.REPORTED;
    }
}
