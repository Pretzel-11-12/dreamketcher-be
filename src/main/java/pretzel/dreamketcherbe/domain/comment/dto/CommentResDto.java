package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;

@Builder
public record CommentResDto(
    Long id,
    Long memberId,
    String content,
    int childCommentCount

) {

    public static CommentResDto of(Comment comment) {
        return CommentResDto.builder()
            .id(comment.getId())
            .memberId(comment.getMember().getId())
            .content(comment.getContent())
            .childCommentCount(comment.getChildCommentCount())
            .build();
    }

}
