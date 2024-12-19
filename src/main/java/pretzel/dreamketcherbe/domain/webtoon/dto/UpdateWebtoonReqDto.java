package pretzel.dreamketcherbe.domain.webtoon.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record UpdateWebtoonReqDto(
    @NotBlank String title,
    @NotBlank MultipartFile thumbnail,
    @NotBlank List<MultipartFile> prologue,
    @NotBlank String story,
    @NotBlank String description
) {

}
