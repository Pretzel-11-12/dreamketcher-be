package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Recommendation;

@Builder
public record CreateRecommendationResDto(
    Long id
) {

    public static CreateRecommendationResDto of(Recommendation recommendation) {
        return CreateRecommendationResDto.builder()
            .id(recommendation.getId())
            .build();
    }
}
