package pretzel.dreamketcherbe.domain.admin.dto;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.admin.entity.ManagementWebtoon;
import pretzel.dreamketcherbe.domain.admin.entity.Reason;
import pretzel.dreamketcherbe.domain.webtoon.entity.SerializationPeriod;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record ManageWebtoonResDto(
    long id,
    String title,
    String genre,
    String authorNickname,
    int episodeCount,
    String createAt,
    String endedAt,
    String updatedAt,
    String status,
    String reason
) {

    public static ManageWebtoonResDto of(Webtoon webtoon, String genre,
        ManagementWebtoon managementWebtoon, SerializationPeriod serializationPeriod) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String reasonContent = Optional.ofNullable(managementWebtoon.getReason())
            .map(Reason::getContent)
            .orElse("N/A");

        String endedAt = Optional.ofNullable(serializationPeriod)
            .map(SerializationPeriod::getEndDate)
            .map(endDate -> endDate.format(formatter))
            .orElse("-");

        return ManageWebtoonResDto.builder()
            .id(webtoon.getId())
            .title(webtoon.getTitle())
            .genre(genre)
            .authorNickname(webtoon.getMember().getName())
            .episodeCount(webtoon.getEpisodeCount())
            .createAt(webtoon.getCreatedAt().format(formatter))
            .endedAt(endedAt)
            .updatedAt(webtoon.getUpdatedAt().format(formatter))
            .status(webtoon.getStatus())
            .reason(reasonContent)
            .build();
    }
}
