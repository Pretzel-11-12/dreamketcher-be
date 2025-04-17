package pretzel.dreamketcherbe.domain.webtoon.dto;

public record SearchedAuthorResDto(
    Long id,
    String authorNickname,
    String profileImage,
    String representativeWorkTitle,
    int workCount
) {

    public static SearchedAuthorResDto of(
        Long id,
        String authorNickname,
        String profileImage,
        String representativeWorkTitle,
        int workCount
    ) {
        return new SearchedAuthorResDto(
            id, authorNickname, profileImage, representativeWorkTitle, workCount
        );
    }
}