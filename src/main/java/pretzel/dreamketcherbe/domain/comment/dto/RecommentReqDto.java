package pretzel.dreamketcherbe.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record RecommentReqDto(
    Long parentCommentId,
    @NotBlank String content
) {

}
