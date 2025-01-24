package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.RecommentRecommendation;

@Builder
public record CreateRecommentRecommendationResDto(
    Long id,
    int recommendationCount
) {

    public static CreateRecommentRecommendationResDto of(
        RecommentRecommendation recommentRecommendation, int recommendationCount) {
        return CreateRecommentRecommendationResDto.builder()
            .id(recommentRecommendation.getId())
            .recommendationCount(recommendationCount)
            .build();
    }
}
