package pretzel.dreamketcherbe.domain.episode.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record CreateEpisodeReqDto(
    @NotBlank String title,
    @NotBlank String thumbnail,
    @NotBlank String content,
    @NotBlank String authorNote,
    @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishedAt
) {

}
