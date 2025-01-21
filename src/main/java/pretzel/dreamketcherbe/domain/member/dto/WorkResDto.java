package pretzel.dreamketcherbe.domain.member.dto;

public record WorkResDto(
    Long id,
    String title,
    String thumbnail,
    String author,
    String description,
    String genre,
    int episodeCount,
    String updatedAt,
    String startedAt,
    Long likeCount,
    Long commentCount,
    Long interestedCount
) {
}
