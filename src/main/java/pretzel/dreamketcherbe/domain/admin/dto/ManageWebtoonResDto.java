package pretzel.dreamketcherbe.domain.admin.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.domain.admin.entity.ManagementWebtoon;
import pretzel.dreamketcherbe.domain.admin.entity.Reason;
import pretzel.dreamketcherbe.domain.webtoon.entity.SerializationPeriod;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Builder
public record ManageWebtoonResDto(
    long id,
    String title,
    List<String> genre,
    String author,
    int episodeCount,
    String createAt,
    String endedAt,
    String updatedAt,
    String status,
    String reason
) {

    public static ManageWebtoonResDto of(Webtoon webtoon, List<String> genres, ManagementWebtoon managementWebtoon, SerializationPeriod serializationPeriod) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String reasonContent = Optional.ofNullable(managementWebtoon.getReason())
            .map(Reason::getContent)
            .orElse("N/A");

        return ManageWebtoonResDto.builder()
            .id(webtoon.getId())
            .title(webtoon.getTitle())
            .genre(genres)
            .author(webtoon.getMember().getName())
            .episodeCount(webtoon.getEpisodeCount())
            .createAt(webtoon.getCreatedAt().format(formatter))
            .endedAt(serializationPeriod.getEndDate().format(formatter))
            .updatedAt(webtoon.getUpdatedAt().format(formatter))
            .status(webtoon.getStatus())
            .reason(reasonContent)
            .build();
    }
}
