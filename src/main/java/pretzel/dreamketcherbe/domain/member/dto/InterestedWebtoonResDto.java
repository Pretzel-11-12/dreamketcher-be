package pretzel.dreamketcherbe.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;

public record InterestedWebtoonResDto(
    Long interestedWebtoonId,
    Long webtoonId,
    String title,
    String thumbnail,
    String authorNickname,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime updatedAt,
    int episodeCount,
    String genre
) {

    public static InterestedWebtoonResDto from(InterestedWebtoon interestedWebtoon,
        String authorNickname, int episodeCount, LocalDateTime updatedAt, String genre) {
        return new InterestedWebtoonResDto(
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