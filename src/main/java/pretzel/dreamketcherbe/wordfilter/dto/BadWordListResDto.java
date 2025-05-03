package pretzel.dreamketcherbe.wordfilter.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.wordfilter.entity.BadWord;

@Builder
public record BadWordListResDto(
    Long id,
    String word,
    String createdAt
) {

    public static BadWordListResDto of(BadWord badWord) {
        return BadWordListResDto.builder()
            .id(badWord.getId())
            .word(badWord.getWord())
            .createdAt(badWord.getCreatedAt().toString())
            .build();
    }

}
