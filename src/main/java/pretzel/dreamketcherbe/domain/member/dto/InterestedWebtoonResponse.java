package pretzel.dreamketcherbe.domain.member.dto;

import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;

// FavoriteWebtoonResponse.java
public record InterestedWebtoonResponse(
    Long interestedWebtoonId,
    Long webtoonId,
    String title,
    String thumbnail,
    String description
) {

    public static InterestedWebtoonResponse from(InterestedWebtoon interestedWebtoon) {
        return new InterestedWebtoonResponse(
            interestedWebtoon.getId(),
            interestedWebtoon.getWebtoon().getId(),
            interestedWebtoon.getWebtoon().getTitle(),
            interestedWebtoon.getWebtoon().getThumbnail(),
            interestedWebtoon.getWebtoon().getDescription()
        );
    }
}