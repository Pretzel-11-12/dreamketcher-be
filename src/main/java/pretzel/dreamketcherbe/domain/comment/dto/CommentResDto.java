package pretzel.dreamketcherbe.domain.comment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;

@Builder
public record CommentResDto(
    Long id,
    String nickname,
    String content,
    int childCommentCount,
    LocalDateTime createdAt

) {

    public static CommentResDto of(Comment comment) {
        return CommentResDto.builder()
            .id(comment.getId())
            .nickname(comment.getMember().getNickname())
            .content(comment.getContent())
            .childCommentCount(comment.getChildCommentCount())
            .createdAt(comment.getCreatedAt())
            .build();
    }

}
