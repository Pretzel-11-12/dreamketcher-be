package pretzel.dreamketcherbe.domain.member.dto;

import java.time.LocalDateTime;
import java.util.List;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;

public record InterestedWebtoonResponse(
    Long interestedWebtoonId,
    Long webtoonId,
    String title,
    String thumbnail,
    String AuthorNickname,
    LocalDateTime updatedAt,
    int episodeCount,
    List<String> genres
) {

    public static InterestedWebtoonResponse from(InterestedWebtoon interestedWebtoon,
        String authorNickname, int episodeCount, LocalDateTime updatedAt, List<String> genres) {
        return new InterestedWebtoonResponse(
            interestedWebtoon.getId(),
            interestedWebtoon.getWebtoon().getId(),
            interestedWebtoon.getWebtoon().getTitle(),
            interestedWebtoon.getWebtoon().getThumbnail(),
            authorNickname,
            updatedAt,
            episodeCount,
            genres
        );
    }
}