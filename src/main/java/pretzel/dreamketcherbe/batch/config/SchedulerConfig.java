package pretzel.dreamketcherbe.batch.config;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@AllArgsConstructor
public class SchedulerConfig {

    private final JobLauncher jobLauncher;

    private Job episodeJob;

    @Scheduled(cron = "0 0 0 * * ?")
    public void runEpisodeJob() {
        try {
            jobLauncher.run(episodeJob, new JobParameters());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
