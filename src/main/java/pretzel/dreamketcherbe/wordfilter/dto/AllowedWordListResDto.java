package pretzel.dreamketcherbe.wordfilter.dto;

import lombok.Builder;
import pretzel.dreamketcherbe.wordfilter.entity.AllowedWord;

@Builder
public record AllowedWordListResDto(
    Long id,
    String word,
    String createdAt
) {

    public static AllowedWordListResDto of(AllowedWord allowedWord) {
        return AllowedWordListResDto.builder()
            .id(allowedWord.getId())
            .word(allowedWord.getWord())
            .createdAt(allowedWord.getCreatedAt().toString())
            .build();
    }

}
