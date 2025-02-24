package pretzel.dreamketcherbe.domain.comment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;

@Builder
public record CommentResDto(
    Long id,
    String nickname,
    String profileImage,
    String content,
    int childCommentCount,
    int recommendationCount,
    int notRecommendationCount,
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
            .createdAt(comment.getCreatedAt())
            .build();
    }

}
