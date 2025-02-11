package pretzel.dreamketcherbe.domain.member.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.common.dto.PageResDto;

@Builder
public record WorkResDto(
        PageResDto<?> content,
        long inSeriesCount,
        long finishCount,
        long newCount,
        long restCount,
        long preSeriesCount
) {

    public static WorkResDto of(PageResDto<?> content, long inSeriesCount, long finishCount, long newCount, long restCount, long preSeriesCount) {
        return WorkResDto.builder()
            .content(content)
            .inSeriesCount(inSeriesCount)
            .finishCount(finishCount)
            .newCount(newCount)
            .restCount(restCount)
            .preSeriesCount(preSeriesCount)
            .build();
    }
}
