package pretzel.dreamketcherbe.domain.member.dto;

public record WorkContentDto(
        Long id,
        String title,
        String thumbnail,
        String author,
        String description,
        String genre,
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
