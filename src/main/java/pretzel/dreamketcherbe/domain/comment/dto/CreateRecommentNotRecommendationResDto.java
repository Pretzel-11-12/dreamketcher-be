package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.RecommentNotRecommendation;

@Builder
public record CreateRecommentNotRecommendationResDto(
    Long id,
    int notRecommendationCount
) {

    public static CreateRecommentNotRecommendationResDto of(
        RecommentNotRecommendation recommentNotRecommendation, int notRecommendationCount) {
        return CreateRecommentNotRecommendationResDto.builder()
            .id(recommentNotRecommendation.getId())
            .notRecommendationCount(notRecommendationCount)
            .build();
    }

}
