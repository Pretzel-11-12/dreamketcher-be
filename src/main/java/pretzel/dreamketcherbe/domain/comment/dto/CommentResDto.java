package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;

@Builder
public record CommentResDto(
    Long id,
    String nickname,
    String content,
    int childCommentCount

) {

    public static CommentResDto of(Comment comment) {
        return CommentResDto.builder()
            .id(comment.getId())
            .nickname(comment.getMember().getNickname())
            .content(comment.getContent())
            .childCommentCount(comment.getChildCommentCount())
            .build();
    }

}
