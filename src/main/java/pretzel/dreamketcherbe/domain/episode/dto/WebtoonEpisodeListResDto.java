package pretzel.dreamketcherbe.domain.episode.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record WebtoonEpisodeListResDto(
    Long webtoonId,
    String webtoonTitle,
    String webtoonThumbnail,
    String webtoonStory,
    int episode_count,
    List<String> genreNames,
    int currentPage,
    int totalPages,
    List<EpisodeInfo> episodes
) {
    public record EpisodeInfo(
        Long episodeId,
        String title,
        String thumbnail,
        LocalDate publishedAt,
        int viewCount,
        long likeCount,
        float averageStar
    ) {
    }
}

