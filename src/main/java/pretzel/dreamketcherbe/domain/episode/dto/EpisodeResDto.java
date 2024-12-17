package pretzel.dreamketcherbe.domain.episode.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

@Builder
public record EpisodeResDto(
    Long id,
    String title,
    String thumbnail,
    String content,
    String authorNote,
    int viewCount
) {

  public static EpisodeResDto of(Episode episode) {
    return EpisodeResDto.builder()
        .id(episode.getId())
        .title(episode.getTitle())
        .thumbnail(episode.getThumbnail())
        .content(episode.getContent())
        .authorNote(episode.getAuthorNote())
        .viewCount(episode.getViewCount())
        .build();
  }
}
