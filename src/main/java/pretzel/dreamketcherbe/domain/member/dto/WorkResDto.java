package pretzel.dreamketcherbe.domain.member.dto;

public record WorkResDto(
    Long id,
    String title,
    String thumbnail,
    int episodeCount,
    String updatedAt,
    String startedAt,
    Long likeCount,
    Long commentCount,
    Long interestedCount
) {
}
