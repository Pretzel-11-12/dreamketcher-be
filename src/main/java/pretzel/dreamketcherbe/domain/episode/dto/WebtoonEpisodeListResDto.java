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
    String AuthorNickname,
    int episode_count,
    int interestCount,
    String genreName,
    int currentPage,
    int totalPages,
    List<EpisodeInfo> episodes
) {

    @Builder
    public record EpisodeInfo(
        Long episodeId,
        String title,
        String thumbnail,
        LocalDate publishedAt,
        long viewCount,
        long likeCount,
        float averageStar
    ) {

        public static EpisodeInfo of(pretzel.dreamketcherbe.domain.episode.entity.Episode episode,
            long likeCount,
            float averageStar
        ) {
            return EpisodeInfo.builder()
                .episodeId(episode.getId())
                .title(episode.getTitle())
                .thumbnail(episode.getThumbnail())
                .publishedAt(episode.getPublishedAt())
                .viewCount(episode.getViewCount())
                .likeCount(likeCount)
                .averageStar(averageStar)
                .build();
        }
    }

    public static WebtoonEpisodeListResDto of(
        Long webtoonId,
        String webtoonTitle,
        String webtoonThumbnail,
        String webtoonStory,
        String AuthorNickname,
        int interestCount,
        int episode_count,
        String genreName,
        int currentPage,
        int totalPages,
        List<EpisodeInfo> episodes
    ) {
        return WebtoonEpisodeListResDto.builder()
            .webtoonId(webtoonId)
            .webtoonTitle(webtoonTitle)
            .webtoonThumbnail(webtoonThumbnail)
            .webtoonStory(webtoonStory)
            .AuthorNickname(AuthorNickname)
            .interestCount(interestCount)
            .episode_count(episode_count)
            .genreName(genreName)
            .currentPage(currentPage)
            .totalPages(totalPages)
            .episodes(episodes)
            .build();
    }
}

