package pretzel.dreamketcherbe.domain.episode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public record UpdateEpisodeReqDto(
    @NotBlank String title,
    @NotBlank String thumbnail,
    @NotBlank List<String> content,
    @NotBlank String authorNote,
    @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishedAt
) {

}
