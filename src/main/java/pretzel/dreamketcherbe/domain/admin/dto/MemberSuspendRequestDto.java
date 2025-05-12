package pretzel.dreamketcherbe.domain.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.report.entity.ReportReasonCode;

public record MemberSuspendRequestDto(
    @NotNull(message = "정지할 일수를 입력해주세요")
    @Min(value = 1, message = "최소한 1일 이상 입력해주세요")
    Integer suspensionDays,

    @NotNull(message = "정지 사유 코드를 선택해주세요")
    ReportReasonCode reasonCode,

    @NotBlank(message = "정지 사유를 입력해주세요")
    @Size(max = 500, message = "정지 사유는 최대 500자까지 입력 가능합니다")
    String suspensionReason
) {

    @Builder
    public MemberSuspendRequestDto {
    }
}
