package pretzel.dreamketcherbe.domain.episode.dto;

import java.time.LocalDate;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

public record BatchEpisodeDto(
    Long id,
    String title,
    String thumbnail,
    String content,
    String authorNote,
    LocalDate publishedAt,
    boolean published,
    Long viewCount
) {

    public static BatchEpisodeDto fromEntity(Episode episode) {
        return new BatchEpisodeDto(
            episode.getId(),
            episode.getTitle(),
            episode.getThumbnail(),
            episode.getContent(),
            episode.getAuthorNote(),
            episode.getPublishedAt(),
            episode.isPublished(),
            episode.getViewCount()
        );
    }

    public Episode toEntity() {
        return Episode.builder()
            .title(title)
            .thumbnail(thumbnail)
            .content(content)
            .authorNote(authorNote)
            .build();

    }
}
