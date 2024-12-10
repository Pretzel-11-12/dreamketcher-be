package pretzel.dreamketcherbe.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final JobLauncher jobLauncher;

    private final Job publishEpisodeJob;

    public SchedulerConfig(JobLauncher jobLauncher, Job publishEpisodeJob) {
        this.jobLauncher = jobLauncher;
        this.publishEpisodeJob = publishEpisodeJob;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void runPublishEpisodeJob() {
        try{
            jobLauncher.run(publishEpisodeJob, new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
