package pretzel.dreamketcherbe.domain.member.dto;

public record StorageItemDetailContentDto(
    Long webtoonId,
    String title,
    String thumbnail,
    String authorNickname,
    String genre,
    int episodeCount,
    String updatedAt
) {

}
