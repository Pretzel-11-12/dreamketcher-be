package pretzel.dreamketcherbe.domain.comment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;

@Builder
public record MyCommentsAndRecommentsListResDto(
    Long webtoonId,
    String title,
    Long episodeId,
    int no,
    String episodeTitle,
    String episodeThumbnail,
    Long commentId,
    Long recommentId,
    String content,
    int childCommentCount,
    int recommendationCount,
    int notRecommendationCount,
    LocalDateTime createdAt,
    String type
) {

    public static MyCommentsAndRecommentsListResDto from(Comment comment) {
        return MyCommentsAndRecommentsListResDto.builder()
            .webtoonId(comment.getWebtoon().getId())
            .title(comment.getWebtoon().getTitle())
            .episodeId(comment.getEpisode().getId())
            .no(comment.getEpisode().getNo())
            .episodeTitle(comment.getEpisode().getTitle())
            .episodeThumbnail(comment.getEpisode().getThumbnail())
            .commentId(comment.getId())
            .recommentId(null)
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
            .webtoonId(recomment.getWebtoon().getId())
            .title(recomment.getWebtoon().getTitle())
            .episodeId(recomment.getEpisode().getId())
            .no(recomment.getEpisode().getNo())
            .episodeTitle(recomment.getEpisode().getTitle())
            .episodeThumbnail(recomment.getEpisode().getThumbnail())
            .content(recomment.getContent())
            .commentId(recomment.getComment().getId())
            .recommentId(recomment.getId())
            .childCommentCount(0)
            .recommendationCount(recomment.getRecommendationCount())
            .notRecommendationCount(recomment.getNotRecommendationCount())
            .createdAt(recomment.getCreatedAt())
            .type("recomment")
            .build();
    }
}
