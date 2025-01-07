package pretzel.dreamketcherbe.domain.member.dto;

import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;

public record InterestedWebtoonSimpleResponse(
    Long interestedWebtoonId,
    Long webtoonId,
    String title,
    String thumbnail
) {

    public static InterestedWebtoonSimpleResponse from(InterestedWebtoon interestedWebtoon) {
        return new InterestedWebtoonSimpleResponse(
            interestedWebtoon.getId(),
            interestedWebtoon.getWebtoon().getId(),
            interestedWebtoon.getWebtoon().getTitle(),
            interestedWebtoon.getWebtoon().getThumbnail()
        );
    }
}
