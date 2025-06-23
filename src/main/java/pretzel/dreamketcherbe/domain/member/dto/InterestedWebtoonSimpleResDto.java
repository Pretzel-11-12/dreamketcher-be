package pretzel.dreamketcherbe.domain.member.dto;

import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;

public record InterestedWebtoonSimpleResDto(
    Long interestedWebtoonId,
    Long webtoonId,
    String title,
    String thumbnail
) {

    public static InterestedWebtoonSimpleResDto from(InterestedWebtoon interestedWebtoon) {
        return new InterestedWebtoonSimpleResDto(
            interestedWebtoon.getId(),
            interestedWebtoon.getWebtoon().getId(),
            interestedWebtoon.getWebtoon().getTitle(),
            interestedWebtoon.getWebtoon().getThumbnail()
        );
    }
}
