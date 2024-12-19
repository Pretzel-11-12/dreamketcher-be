package pretzel.dreamketcherbe.domain.webtoon.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record CreateWebtoonReqDto(
    @NotBlank String title,
    MultipartFile thumbnail,
    List<MultipartFile> prologue,
    @NotBlank String story,
    @NotBlank String description
) {

}
