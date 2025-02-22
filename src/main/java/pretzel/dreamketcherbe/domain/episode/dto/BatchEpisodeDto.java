package pretzel.dreamketcherbe.domain.episode.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

public record BatchEpisodeDto(
    Long id,
    int no,
    String webtoonTitle,
    String title,
    String thumbnail,
    String content,
    String authorName,
    String authorNote,
    String authorImage,
    LocalDate publishedAt,
    boolean published,
    int likeCount,
    Long viewCount,
    float averageStar

) {

    public static BatchEpisodeDto of(Episode episode) {
        return new BatchEpisodeDto(
            episode.getId(),
            episode.getNo(),
            episode.getWebtoon().getTitle(),
            episode.getTitle(),
            episode.getThumbnail(),
            episode.getContent(),
            episode.getWebtoon().getMember().getName(),
            episode.getAuthorNote(),
            episode.getWebtoon().getMember().getImageUrl(),
            episode.getPublishedAt(),
            episode.isPublished(),
            episode.getLikeCount(),
            episode.getViewCount(),
            episode.getAverageStar()
        );
    }

}
