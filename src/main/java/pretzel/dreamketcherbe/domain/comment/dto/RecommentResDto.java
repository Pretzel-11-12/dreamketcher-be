package pretzel.dreamketcherbe.domain.comment.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;

@Builder
public record RecommentResDto(
    Long id,
    String ninkname,
    String content,
    Long parentCommentId,
    int commentOrder
) {

    public static RecommentResDto of(Recomment recomment) {
        return RecommentResDto.builder()
            .id(recomment.getId())
            .ninkname(recomment.getMember().getNickname())
            .content(recomment.getContent())
            .parentCommentId(recomment.getParentCommentId())
            .commentOrder(recomment.getCommentOrder())
            .build();
    }

}
