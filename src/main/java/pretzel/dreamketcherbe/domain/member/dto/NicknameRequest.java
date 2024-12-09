package pretzel.dreamketcherbe.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NicknameRequest(
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(max = 10, message = "닉네임은 최대 10글자까지 허용됩니다.")
    @Pattern(
        regexp = "^[a-zA-Z0-9가-힣]+$",
        message = "닉네임에는 특수문자나 공백이 포함될 수 없습니다."
    )
    String nickname
) {
}