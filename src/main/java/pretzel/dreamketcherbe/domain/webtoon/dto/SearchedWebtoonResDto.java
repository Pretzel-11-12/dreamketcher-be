package pretzel.dreamketcherbe.domain.webtoon.dto;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Builder
public record SearchedWebtoonResDto(
    Long id,
    String thumbnail,
    String authorNickname,
    String title,
    String story,
    String genre,
    int lastEpisode,
    float averageStar,
    Long numOfStars,
    List<TagDto> tags
) {

    public static SearchedWebtoonResDto of(Webtoon webtoon, String genreName, Long numOfStars) {
        List<TagDto> tagDtos = webtoon.getWebtoonTags()
            .stream()
            .map(wt -> TagDto.from(wt.getTag()))
            .collect(Collectors.toList());

        return SearchedWebtoonResDto.builder()
            .id(webtoon.getId())
            .thumbnail(webtoon.getThumbnail())
            .authorNickname(webtoon.getMember().getNickname())
            .story(webtoon.getStory())
            .title(webtoon.getTitle())
            .genre(genreName)
            .lastEpisode(webtoon.getEpisodeCount())
            .averageStar(webtoon.getAverageStar())
            .numOfStars(numOfStars)
            .tags(tagDtos)
            .build();
    }
}
