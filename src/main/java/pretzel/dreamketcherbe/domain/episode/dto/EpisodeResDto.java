package pretzel.dreamketcherbe.domain.episode.dto;

import java.util.List;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

@Builder
public record EpisodeResDto(
    Long id,
    int no,
    String title,
    String thumbnail,
    String content,
    String authorNote,
    long viewCount
) {

    public static EpisodeResDto of(Episode episode) {
        return EpisodeResDto.builder()
            .id(episode.getId())
            .title(episode.getTitle())
            .no(episode.getNo())
            .thumbnail(episode.getThumbnail())
            .content(episode.getContent())
            .authorNote(episode.getAuthorNote())
            .viewCount(episode.getViewCount())
            .build();
    }
}
