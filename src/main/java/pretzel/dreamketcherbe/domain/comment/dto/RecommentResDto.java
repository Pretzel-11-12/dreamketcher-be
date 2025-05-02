package pretzel.dreamketcherbe.domain.comment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;
import pretzel.dreamketcherbe.domain.comment.entity.RecommentStatus;

@Builder
public record RecommentResDto(
    Long id,
    String nickname,
    String profileImage,
    String content,
    Long parentCommentId,
    int commentOrder,
    int recommendationCount,
    int notRecommendationCount,
    RecommentStatus status,
    LocalDateTime createdAt
) {

    /**
     * 주어진 Recomment 엔티티로부터 RecommentResDto 객체를 생성합니다.
     *
     * @param recomment DTO로 변환할 Recomment 엔티티
     * @return Recomment 엔티티의 정보를 담은 RecommentResDto 인스턴스
     */
    public static RecommentResDto of(Recomment recomment) {
        return RecommentResDto.builder()
            .id(recomment.getId())
            .nickname(recomment.getMember().getNickname())
            .profileImage(recomment.getMember().getImageUrl())
            .content(recomment.getContent())
            .parentCommentId(recomment.getParentCommentId())
            .commentOrder(recomment.getCommentOrder())
            .recommendationCount(recomment.getRecommendationCount())
            .notRecommendationCount(recomment.getNotRecommendationCount())
            .status(recomment.getStatus())
            .createdAt(recomment.getCreatedAt())
            .build();
    }

}
