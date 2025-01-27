package pretzel.dreamketcherbe.domain.ranking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
public class WebtoonPopularityDataDto {

    private Long id;
    private String title;
    private String member;
    private String description;
    private String thumbnail;
    private String genre;
    private int lastEpisode;
    private float averageStar;
    private Long numOfStars;
    private Long likeCount;
    private Long viewCount;
    private Long interestedCount;
    private float popularity;

    public WebtoonPopularityDataDto(Long id, String title, String member, String description, String thumbnail, String genre, int lastEpisode,
                                    float averageStar, Long numOfStars, Long likeCount, Long viewCount,
                                    Long interestedCount, float popularity) {
        this.id = id;
        this.title = title;
        this.member = member;
        this.description = description;
        this.thumbnail = thumbnail;
        this.genre = genre;
        this.lastEpisode = lastEpisode;
        this.averageStar = averageStar;
        this.numOfStars = numOfStars;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.interestedCount = interestedCount;
        this.popularity = popularity;
    }
}