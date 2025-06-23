package pretzel.dreamketcherbe.domain.member.dto;

/**
 * Webtoon 작업 정보 + 현재 Webtoon 상태를 포함한 DTO.
 * 기존 WorkContentDto 에서 status 필드만 추가됨.
 * 다른 곳의 응답에는 영향을 주지 않도록 별도 record 로 분리.
 */
public record WorkContentWithStatusDto(
        Long id,
        String title,
        String thumbnail,
        String authorNickname,
        String story,
        String genre,
        String status,
        int episodeCount,
        String updatedAt,
        String startedAt,
        Float avgStar,
        Long numOfStars,
        Long likeCount,
        Long commentCount,
        Long interestedCount
) {
}
