package pretzel.dreamketcherbe.domain.member.dto;

public record StorageItemContentDto(
    Long webtoonId,
    String title,
    String thumbnail,
    String authorNickname
) {

}
