package pretzel.dreamketcherbe.batch.config;

import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import pretzel.dreamketcherbe.domain.episode.dto.BatchEpisodeDto;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class EpisodeBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;

    /**
     * 미발행 에피소드 읽기
     */
    @Bean
    public ItemReader<BatchEpisodeDto> episodeItemReader() {

        if (entityManagerFactoryBean.getObject() == null) {
            throw new IllegalStateException("EntityManagerFactory must not be null");
        }

        return new JpaPagingItemReaderBuilder<BatchEpisodeDto>()
            .name("episodeItemReader")
            .entityManagerFactory(entityManagerFactoryBean.getObject())
            .queryString(
                "SELECT e FROM episodes e WHERE e.publishedAt = :today AND e.published = false")
            .parameterValues(Map.of("today", LocalDate.now()))
            .pageSize(10)
            .build();
    }

    /**
     * published 상태 업데이트
     */
    @Bean
    public ItemProcessor<BatchEpisodeDto, BatchEpisodeDto> episodeEpisodeItemProcessor() {
        return dto -> new BatchEpisodeDto(
            dto.id(),
            dto.title(),
            dto.thumbnail(),
            dto.content(),
            dto.authorNote(),
            dto.publishedAt(),
            true,
            dto.viewCount()
        );
    }

    /**
     * 에피소드 업데이트 저장
     */
    @Bean
    public ItemWriter<BatchEpisodeDto> episodeItemWriter(EpisodeRepository episodeRepository) {
        return items -> items.forEach(dto -> {
            Episode episode = episodeRepository.findById(dto.id())
                .orElseThrow(
                    () -> new IllegalStateException("Episode not found with id: " + dto.id()));
            episode.setPublished(dto.published());
            episodeRepository.save(episode);
        });
    }

    /**
     * Step : Chunk 기반 처리
     */
    @Bean
    public Step episodeStep(ItemReader<BatchEpisodeDto> reader,
        ItemProcessor<BatchEpisodeDto, BatchEpisodeDto> processor,
        ItemWriter<BatchEpisodeDto> writer) {
        return new StepBuilder("episodeStep", jobRepository)
            .<BatchEpisodeDto, BatchEpisodeDto>chunk(10, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }

    /**
     * Job
     */
    @Bean
    public Job episodeJob(Step episodeStep) {
        return new JobBuilder("episodeJob", jobRepository)
            .start(episodeStep)
            .build();
    }

}
