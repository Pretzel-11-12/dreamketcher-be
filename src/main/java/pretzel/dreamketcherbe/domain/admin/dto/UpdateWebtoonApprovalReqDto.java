package pretzel.dreamketcherbe.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdateWebtoonApprovalReqDto(
        @NotBlank List<Long> webtoonIds,
        @NotBlank String approval,
        String reason,
        String detailReason
) {
}
