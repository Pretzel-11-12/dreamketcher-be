package pretzel.dreamketcherbe.batch.config;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import pretzel.dreamketcherbe.domain.episode.dto.BatchEpisodeDto;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeException;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeExceptionType;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;


@Slf4j
@Configuration
@AllArgsConstructor
public class EpisodeBatchConfig {

    private final PlatformTransactionManager transactionManager;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;

    /**
     * 미발행 에피소드 읽기
     */
    @Bean
    public JpaPagingItemReader<Episode> episodeItemReader() {

        if (entityManagerFactoryBean.getObject() == null) {
            throw new IllegalStateException("entity manager factory been 이 null 입니다.");
        }

        return new JpaPagingItemReaderBuilder<Episode>()
            .name("episodeItemReader")
            .entityManagerFactory(entityManagerFactoryBean.getObject())
            .queryString(
                "SELECT e FROM Episode e " +
                    "JOIN FETCH e.webtoon w " +
                    "LEFT JOIN FETCH w.member m " +
                    "WHERE FUNCTION('DATE', e.publishedAt) = :today " +
                    "AND e.published = false")
            .parameterValues(Map.of("today", LocalDate.now()))
            .pageSize(10)
            .build();
    }

    /**
     * published 상태 업데이트
     */
    @Bean
    public ItemProcessor<Episode, BatchEpisodeDto> episodeEpisodeItemProcessor() {

        return episode -> new BatchEpisodeDto(
            episode.getId(),
            episode.getNo(),
            episode.getWebtoon().getTitle(),
            episode.getTitle(),
            episode.getThumbnail(),
            episode.getContent(),
            episode.getWebtoon().getMember().getName(),
            episode.getAuthorNote(),
            episode.getMember().getImageUrl(),
            episode.getPublishedAt(),
            true,  // published 상태 업데이트
            episode.getLikeCount(),
            episode.getViewCount(),
            episode.getAverageStar()
        );
    }

    /**
     * 에피소드 업데이트 저장
     */
    @Transactional
    @Bean
    public ItemWriter<BatchEpisodeDto> episodeItemWriter(EpisodeRepository episodeRepository) {
        return items -> {
            if (items.isEmpty()) {
                log.warn("아이템이 존재하지 않습니다.");
                return;
            }

            List<Long> episodeIds = items.getItems().stream().map(BatchEpisodeDto::id).toList();
            List<Episode> episodes = episodeRepository.findAllById(episodeIds);

            Map<Long, Episode> episodeMap = episodes.stream()
                .collect(Collectors.toMap(Episode::getId, e -> e));

            items.forEach(dto -> {
                Episode episode = episodeMap.get(dto.id());
                if (episode == null) {
                    throw new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND);
                }
                episode.updatePublished(dto.published());
            });
            episodeRepository.updatePublishedById(episodeIds);
        };
    }


    /**
     * Step : Chunk 기반 처리
     */
    @Bean
    public Step episodeStep(JobRepository jobRepository,
        JpaPagingItemReader<Episode> reader,
        ItemProcessor<Episode, BatchEpisodeDto> processor,
        ItemWriter<BatchEpisodeDto> writer) {
        return new StepBuilder("episodeStep", jobRepository)
            .<Episode, BatchEpisodeDto>chunk(10, transactionManager)
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
            .incrementer(new RunIdIncrementer()) // 실행 마다 새로운 JobInstance 생성
            .start(episodeStep)
            .build();
    }
}
