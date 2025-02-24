package pretzel.dreamketcherbe.domain.episode.dto;

import java.util.List;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

@Builder
public record EpisodeResDto(
    Long id,
    int no,
    String webtoonTitle,
    String webtoonThumbnail,
    String genre,
    String title,
    String thumbnail,
    String content,
    String authorNote,
    String authorName,
    String authorImage,
    int likeCount,
    float averageStar,
    long viewCount
) {

    public static EpisodeResDto of(Episode episode) {
        return EpisodeResDto.builder()
            .id(episode.getId())
            .webtoonTitle(episode.getWebtoon().getTitle())
            .webtoonThumbnail(episode.getWebtoon().getThumbnail())
            .genre(episode.getWebtoon().getGenre().getName())
            .title(episode.getTitle())
            .no(episode.getNo())
            .thumbnail(episode.getThumbnail())
            .content(episode.getContent())
            .authorNote(episode.getAuthorNote())
            .authorName(episode.getMember().getName())
            .authorImage(episode.getMember().getImageUrl())
            .likeCount(episode.getLikeCount())
            .averageStar(episode.getAverageStar())
            .viewCount(episode.getViewCount())
            .build();
    }
}
