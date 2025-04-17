package pretzel.dreamketcherbe.domain.webtoon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SearchedReqDto(
    @NotBlank(message = "검색어는 필수입니다.")
    @Size(min = 2, message = "검색어는 2글자 이상 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "검색어는 한글, 영문, 숫자만 입력해주세요.")
    String keyword
) {

}
