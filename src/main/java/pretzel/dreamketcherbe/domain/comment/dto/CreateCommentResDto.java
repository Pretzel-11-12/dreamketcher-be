package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;

@Builder
public record CreateCommentResDto(
    Long id
) {

    public static CreateCommentResDto of(Comment comment) {
        return CreateCommentResDto.builder()
            .id(comment.getId())
            .build();
    }
}
