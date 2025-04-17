package pretzel.dreamketcherbe.domain.member.dto;

public record WorkContentDto(
    Long id,
    String title,
    String thumbnail,
    String authorNickname,
    String story,
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
