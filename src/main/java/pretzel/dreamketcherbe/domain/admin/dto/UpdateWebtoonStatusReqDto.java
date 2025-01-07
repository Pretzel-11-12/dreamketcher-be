package pretzel.dreamketcherbe.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdateWebtoonStatusReqDto(
    @NotBlank List<Long> webtoonIds,
    @NotBlank String status
) {

}
