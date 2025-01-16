package pretzel.dreamketcherbe.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    String nickname,

    @Size(max = 255, message = "짧은 소개는 255자 이내로 입력해주세요.")
    String shortIntroduction,

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String businessEmail
) {

}
