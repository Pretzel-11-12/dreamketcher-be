package pretzel.dreamketcherbe.domain.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebtoonPopularityDataDto {

    private String title;
    private String thumbnail;
    private List<String> genres;
    private int lastEpisode;
    private float averageStar;
    private int numOfStars;
    private int likeCount;
    private int viewCount;
    private int interestedCount;
    private float popularity;

    public static WebtoonPopularityDataDto of(Object[] record) {
        return WebtoonPopularityDataDto.builder()
            .title((String) record[0])
            .thumbnail((String) record[1])
            .genres(record[2] != null
                ? Arrays.asList(((String) record[2]).split(","))
                : Collections.emptyList())
            .lastEpisode(((Number) record[3]).intValue())
            .averageStar(((Number) record[4]).floatValue())
            .numOfStars(((Number) record[5]).intValue())
            .likeCount(((Number) record[6]).intValue())
            .viewCount(record[7] != null ? ((Number) record[7]).intValue() : 0)
            .interestedCount(((Number) record[8]).intValue())
            .popularity(calculatePopularity(
                ((Number) record[4]).floatValue(),
                ((Number) record[6]).intValue(),
                record[7] != null ? ((Number) record[7]).intValue() : 0,
                record[8] != null ? ((Number) record[8]).intValue() : 0
            ))
            .build();
    }

    private static float calculatePopularity(float averageStar, int likeCount, int viewCount, int interestedCount) {
        return (float) ((averageStar * 0.3) + (likeCount * 0.3) + (viewCount * 0.1) + (interestedCount * 0.1));
    }
}