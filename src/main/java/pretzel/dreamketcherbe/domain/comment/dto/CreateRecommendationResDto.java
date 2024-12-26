package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Recommendation;

@Builder
public record CreateRecommendationResDto(
    Long id,
    int recommendationCount
) {

    public static CreateRecommendationResDto of(Recommendation recommendation,
        int recommendationCount) {
        return CreateRecommendationResDto.builder()
            .recommendationCount(recommendationCount)
            .id(recommendation.getId())
            .build();
    }
}
