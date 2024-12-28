package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.RecommentRecommendation;

@Builder
public record CreateRecommentRecommendationResDto(
    Long id,
    int recommentRecommendationCount
) {

    public static CreateRecommentRecommendationResDto of(
        RecommentRecommendation recommentRecommendation, int recommentRecommendationCount) {
        return CreateRecommentRecommendationResDto.builder()
            .id(recommentRecommendation.getId())
            .recommentRecommendationCount(recommentRecommendationCount)
            .build();
    }
}
