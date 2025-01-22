package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.NotRecommendation;

@Builder
public record NotRecommendationResDto(
    Long id,
    int notRecommendationCount
) {

    public static NotRecommendationResDto of(NotRecommendation notRecommendation,
        int notRecommendationCount) {
        return NotRecommendationResDto.builder()
            .id(notRecommendation.getId())
            .notRecommendationCount(notRecommendationCount)
            .build();
    }
}
