package pretzel.dreamketcherbe.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateFolderReqDto(
    @NotNull(message = "폴더 이름은 필수값입니다.")
    @Size(min = 1, max = 10, message = "폴더 이름은 10자 이하여야 합니다.")
    String folderName
) {
}
