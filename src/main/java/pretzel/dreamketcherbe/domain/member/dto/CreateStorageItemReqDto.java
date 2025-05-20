package pretzel.dreamketcherbe.domain.member.dto;

import jakarta.validation.constraints.NotNull;

public record CreateStorageItemReqDto(
    @NotNull(message = "웹툰은 필수 입력값입니다.")
    Long webtoonId
) {

}
