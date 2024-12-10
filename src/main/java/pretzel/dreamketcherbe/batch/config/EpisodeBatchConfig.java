package pretzel.dreamketcherbe.batch.config;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pretzel.dreamketcherbe.batch.processor.EpisodeItemProcessor;
import pretzel.dreamketcherbe.batch.reader.EpisodeItemReader;
import pretzel.dreamketcherbe.batch.writer.EpisodeItemWriter;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class EpisodeBatchConfig {

    private final JobBuilder jobBuilder;

    private final StepBuilder stepBuilder;

    @Bean
    public Job publishEpisodeJob(JobBuilder jobBuilder, Step publishEpisodeStep) {
        return jobBuilder.get("publishEpisodeJob").start(publishEpisodeStep).build();
    }

    @Bean
    public Step publishEpisodeStep(StepBuilder stepBuilder, EpisodeItemReader reader,
        EpisodeItemProcessor processor, EpisodeItemWriter writer) {
        return stepBuilder.get("publishEpisodeStep")
            .<Episode, Episode>chunk(10) // Chunk size
            .reader(reader).processor(processor).writer(writer).build();
    }

}
