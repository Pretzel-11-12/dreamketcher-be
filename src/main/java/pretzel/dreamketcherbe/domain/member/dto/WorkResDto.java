package pretzel.dreamketcherbe.domain.member.dto;

public record WorkResDto(
    Long id,
    int no,
    String title,
    String thumbnail,
    String updatedAt,
    String startedAt,
    Long viewCount,
    Long commentCount,
    Long interestedCount
) {
}
