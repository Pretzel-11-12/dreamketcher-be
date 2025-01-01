package pretzel.dreamketcherbe.domain.comment.dto;

public record RecommentReqDto(
    Long parentCommentId,
    String content
) {

}
