package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;

@Builder
public record CreateRecommentResDto(
    Long id
) {

    public static CreateRecommentResDto of(Recomment recomment) {
        return CreateRecommentResDto
            .builder()
            .id(recomment.getId())
            .build();
    }
}
