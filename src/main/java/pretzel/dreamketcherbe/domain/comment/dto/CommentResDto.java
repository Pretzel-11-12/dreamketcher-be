package pretzel.dreamketcherbe.domain.comment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;
import pretzel.dreamketcherbe.domain.comment.entity.CommentStatus;

@Builder
public record CommentResDto(
    Long id,
    String nickname,
    String profileImage,
    String content,
    int childCommentCount,
    int recommendationCount,
    int notRecommendationCount,
    CommentStatus status,
    LocalDateTime createdAt

) {

    /**
     * Comment 엔티티로부터 CommentResDto 인스턴스를 생성합니다.
     *
     * @param comment 변환할 Comment 엔티티
     * @return 주어진 Comment의 정보를 담은 CommentResDto 객체
     */
    public static CommentResDto of(Comment comment) {
        return CommentResDto.builder()
            .id(comment.getId())
            .nickname(comment.getMember().getNickname())
            .profileImage(comment.getMember().getImageUrl())
            .content(comment.getContent())
            .childCommentCount(comment.getChildCommentCount())
            .recommendationCount(comment.getRecommendationCount())
            .notRecommendationCount(comment.getNotRecommendationCount())
            .status(comment.getStatus())
            .createdAt(comment.getCreatedAt())
            .build();
    }

}
