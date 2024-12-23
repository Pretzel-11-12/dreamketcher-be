package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.RecommentRecommendation;

@Builder
public record CreateRecommentRecommendationResDto(
    Long id
) {

    public static CreateRecommentRecommendationResDto of(
        RecommentRecommendation recommentRecommendation) {
        return CreateRecommentRecommendationResDto.builder()
            .id(recommentRecommendation.getId())
            .build();
    }
}
