package pretzel.dreamketcherbe.batch.config;

import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
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

    private final PlatformTransactionManager transactionManager;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;

    /**
     * 미발행 에피소드 읽기
     */
    @StepScope
    @Bean
    public JpaPagingItemReader<BatchEpisodeDto> episodeItemReader() {

        if (entityManagerFactoryBean.getObject() == null) {
            throw new IllegalStateException("entity manager factory been 이 null 입니다.");
        }

        return new JpaPagingItemReaderBuilder<BatchEpisodeDto>()
            .name("episodeItemReader")
            .entityManagerFactory(entityManagerFactoryBean.getObject())
            .queryString(
                "SELECT e FROM episodes e WHERE e.publishedAt = :today AND e.published = false AND e.status = 'APPROVAL'")
            .parameterValues(Map.of("today", LocalDate.now()))
            .pageSize(10)
            .build();
    }

    /**
     * published 상태 업데이트
     */
    @StepScope
    @Bean
    public ItemProcessor<BatchEpisodeDto, BatchEpisodeDto> episodeEpisodeItemProcessor() {
        return dto -> new BatchEpisodeDto(
            dto.id(),
            dto.no(),
            dto.webtoonTitle(),
            dto.title(),
            dto.thumbnail(),
            dto.content(),
            dto.authorName(),
            dto.authorNote(),
            dto.authorImage(),
            dto.publishedAt(),
            true,
            dto.likeCount(),
            dto.viewCount(),
            dto.averageStar()
        );
    }

    /**
     * 에피소드 업데이트 저장
     */
    @StepScope
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
    @JobScope
    @Bean
    public Step episodeStep(JobRepository jobRepository,
        ItemReader<BatchEpisodeDto> reader,
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
    public Job episodeJob(JobRepository jobRepository, Step episodeStep) {
        return new JobBuilder("episodeJob", jobRepository)
            .start(episodeStep)
            .build();
    }

}
