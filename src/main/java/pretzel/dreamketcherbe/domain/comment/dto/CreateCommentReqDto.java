package pretzel.dreamketcherbe.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentReqDto(
    @NotBlank String content
) {

}
