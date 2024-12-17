package pretzel.dreamketcherbe.domain.admin.dto;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.admin.entity.ManagementEpisode;
import pretzel.dreamketcherbe.domain.admin.entity.Reason;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

@Builder
public record ManageEpisodeResDto(
    Long episodeId,
    String title,
    Long starCount,
    Long interestedCount,
    Long likeCount,
    String publishedAt,
    String createdAt,
    String updatedAt,
    String status,
    String reason
) {

    public static ManageEpisodeResDto of(Episode episode, ManagementEpisode managementEpisode) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String reasonContent = Optional.ofNullable(managementEpisode.getReason())
            .map(Reason::getContent)
            .orElse("N/A");

        return ManageEpisodeResDto.builder()
            .episodeId(episode.getId())
            .title(episode.getTitle())
            .starCount(0L)
            .interestedCount(0L)
            .likeCount(0L)
            .publishedAt(episode.getPublishedAt().format(formatter))
            .createdAt(episode.getCreatedAt().format(formatter))
            .updatedAt(episode.getUpdatedAt().format(formatter))
            .status(episode.getStatus())
            .reason(reasonContent)
            .build();
    }
}
