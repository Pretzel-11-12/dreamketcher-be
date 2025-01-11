package pretzel.dreamketcherbe.domain.comment.dto;

public record CreateRecommentReqDto(
    Long parentCommentId,
    String content
) {

}
