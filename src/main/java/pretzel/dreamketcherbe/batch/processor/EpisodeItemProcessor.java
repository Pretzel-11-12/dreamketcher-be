package pretzel.dreamketcherbe.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import pretzel.dreamketcherbe.domain.episode.dto.BatchEpisodeDto;

public class EpisodeItemProcessor implements ItemProcessor<BatchEpisodeDto, BatchEpisodeDto> {

    @Override
    public BatchEpisodeDto process(BatchEpisodeDto episode) throws Exception {
        return new BatchEpisodeDto(
            episode.id(),
            episode.title(),
            episode.thumbnail(),
            episode.authorNote(),
            episode.content(),
            episode.publishedAt(),
            true,
            episode.viewCount()
        );
    }
}
