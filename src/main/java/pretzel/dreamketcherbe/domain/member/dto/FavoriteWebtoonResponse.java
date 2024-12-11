package pretzel.dreamketcherbe.domain.member.dto;

import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;

// FavoriteWebtoonResponse.java
public record FavoriteWebtoonResponse(
    Long interestedWebtoonId,
    Long webtoonId,
    String title,
    String thumbnail,
    String description
) {
    public static FavoriteWebtoonResponse from(InterestedWebtoon interestedWebtoon) {
        return new FavoriteWebtoonResponse(
            interestedWebtoon.getId(),
            interestedWebtoon.getWebtoonId().getId(),
            interestedWebtoon.getWebtoonId().getTitle(),
            interestedWebtoon.getWebtoonId().getThumbnail(),
            interestedWebtoon.getWebtoonId().getDescription()
        );
    }
}