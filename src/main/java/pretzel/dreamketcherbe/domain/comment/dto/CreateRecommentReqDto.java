package pretzel.dreamketcherbe.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRecommentReqDto(
    @NotBlank String content
) {

}
