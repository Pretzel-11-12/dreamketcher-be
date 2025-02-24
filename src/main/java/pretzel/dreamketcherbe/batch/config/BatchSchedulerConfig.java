package pretzel.dreamketcherbe.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class BatchSchedulerConfig {

    private final JobLauncher jobLauncher;

    private final Job episodeJob;

    @Autowired
    public BatchSchedulerConfig(JobLauncher jobLauncher, @Qualifier("episodeJob") Job episodeJob) {
        this.jobLauncher = jobLauncher;
        this.episodeJob = episodeJob;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void runEpisodeJob() {
        try {
            jobLauncher.run(episodeJob, new JobParameters());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
