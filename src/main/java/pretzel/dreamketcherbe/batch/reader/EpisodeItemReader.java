package pretzel.dreamketcherbe.batch.reader;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;

@Component
@AllArgsConstructor
public class EpisodeItemReader implements ItemReader<Episode> {

    private final EpisodeRepository episodeRepository;

    private List<Episode> episodes;

    @Override
    public Episode read() {
        if (episodes == null) {
            episodes = episodeRepository.findByPublishedAtAndPublishedFalse(LocalDate.now());
        }
        return episodes.isEmpty() ? null : episodes.remove(0);
    }
}
