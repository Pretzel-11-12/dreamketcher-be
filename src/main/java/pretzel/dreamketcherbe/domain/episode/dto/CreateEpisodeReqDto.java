package pretzel.dreamketcherbe.domain.episode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record CreateEpisodeReqDto(
    @NotBlank String title,
    @NotBlank String thumbnail,
    @NotEmpty String content,
    @NotBlank String authorNote,
    @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishedAt
) {

}
