package pretzel.dreamketcherbe.domain.episode.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public record CreateEpisodeReqDto(
    @NotBlank String title,
    @NotBlank MultipartFile thumbnail,
    @NotBlank List<MultipartFile> content,
    @NotBlank String authorNote,
    @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishedAt
) {

}
