package pretzel.dreamketcherbe.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;

import java.time.LocalDateTime;

public record InterestedWebtoonResponse(
    Long interestedWebtoonId,
    Long webtoonId,
    String title,
    String thumbnail,
    String AuthorNickname,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime updatedAt,
    int episodeCount,
    String genre
) {

    public static InterestedWebtoonResponse from(InterestedWebtoon interestedWebtoon,
        String authorNickname, int episodeCount, LocalDateTime updatedAt, String genre) {
        return new InterestedWebtoonResponse(
            interestedWebtoon.getId(),
            interestedWebtoon.getWebtoon().getId(),
            interestedWebtoon.getWebtoon().getTitle(),
            interestedWebtoon.getWebtoon().getThumbnail(),
            authorNickname,
            updatedAt,
            episodeCount,
            genre
        );
    }
}