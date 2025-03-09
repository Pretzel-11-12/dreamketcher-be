package pretzel.dreamketcherbe.domain.comment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;

@Builder
public record MyCommentsAndRecommentsListResDto(
    Long id,
    String content,
    int childCommentCount,
    int recommendationCount,
    int notRecommendationCount,
    LocalDateTime createdAt,
    String type
) {

    public static MyCommentsAndRecommentsListResDto from(Comment comment) {
        return MyCommentsAndRecommentsListResDto.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .childCommentCount(comment.getChildCommentCount())
            .recommendationCount(comment.getRecommendationCount())
            .notRecommendationCount(comment.getNotRecommendationCount())
            .createdAt(comment.getCreatedAt())
            .type("comment")
            .build();
    }

    public static MyCommentsAndRecommentsListResDto from(Recomment recomment) {
        return MyCommentsAndRecommentsListResDto.builder()
            .id(recomment.getId())
            .content(recomment.getContent())
            .childCommentCount(0)
            .recommendationCount(recomment.getRecommendationCount())
            .notRecommendationCount(recomment.getNotRecommendationCount())
            .createdAt(recomment.getCreatedAt())
            .type("recomment")
            .build();
    }
}
