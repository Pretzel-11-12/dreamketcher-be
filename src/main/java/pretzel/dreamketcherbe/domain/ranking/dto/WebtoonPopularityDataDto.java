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

    private Long id;
    private String title;
    private String member;
    private String description;
    private String thumbnail;
    private List<String> genres;
    private int lastEpisode;
    private float averageStar;
    private Long numOfStars;
    private Long likeCount;
    private Long viewCount;
    private Long interestedCount;
    private float popularity;

    public WebtoonPopularityDataDto(Long id, String title, String member, String description, String thumbnail, String genres, int lastEpisode,
                                    float averageStar, Long numOfStars, Long likeCount, Long viewCount,
                                    Long interestedCount, float popularity) {
        this.id = id;
        this.title = title;
        this.member = member;
        this.description = description;
        this.thumbnail = thumbnail;
        this.genres = genres != null ? Arrays.asList(genres.split(",")) : Collections.emptyList();
        this.lastEpisode = lastEpisode;
        this.averageStar = averageStar;
        this.numOfStars = numOfStars;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.interestedCount = interestedCount;
        this.popularity = popularity;
    }
}