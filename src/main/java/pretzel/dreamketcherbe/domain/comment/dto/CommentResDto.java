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
