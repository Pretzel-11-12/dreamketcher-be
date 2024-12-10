package pretzel.dreamketcherbe.batch.writer;

import java.util.List;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import pretzel.dreamketcherbe.domain.episode.dto.BatchEpisodeDto;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;

@Component
public class EpisodeItemWriter implements ItemWriter<BatchEpisodeDto> {

    private final EpisodeRepository episodeRepository;

    public EpisodeItemWriter(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    @Override
    public void write(Chunk<? extends BatchEpisodeDto> items) {
        List<Episode> episodes = items.getItems().stream()
            .map(BatchEpisodeDto::toEntity)
            .toList();
        episodeRepository.saveAll(episodes);
    }

}
