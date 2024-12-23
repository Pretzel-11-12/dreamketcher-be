package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.RecommentNotRecommendation;

@Builder
public record CreateRecommentNotRecommendationResDto(
    Long id
) {

    public static CreateRecommentNotRecommendationResDto of(
        RecommentNotRecommendation recommentNotRecommendation) {
        return CreateRecommentNotRecommendationResDto.builder()
            .id(recommentNotRecommendation.getId())
            .build();
    }

}
