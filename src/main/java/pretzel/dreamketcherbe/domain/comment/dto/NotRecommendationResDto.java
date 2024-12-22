package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.NotRecommendation;

@Builder
public record NotRecommendationResDto(
    Long id
) {

    public static NotRecommendationResDto of(NotRecommendation notRecommendation) {
        return NotRecommendationResDto.builder()
            .id(notRecommendation.getId())
            .build();
    }
}
