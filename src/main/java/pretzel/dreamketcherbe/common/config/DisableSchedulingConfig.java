package pretzel.dreamketcherbe.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

/**
 * 임시로 모든 스케줄링 작업을 중지시키는 설정 클래스 이 클래스는 @EnableScheduling 설정을 오버라이드하여 실제 스케줄링 작업이 실행되지 않도록 함
 */
@Configuration
public class DisableSchedulingConfig {

    /**
     * 스케줄링 작업이 실행되지 않도록 TaskScheduler를 오버라이드
     *
     * @return 아무 작업도 실행하지 않는 TaskScheduler
     */
    @Bean
    @Primary
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler() {
            @Override
            public void schedule(Runnable task, org.springframework.scheduling.Trigger trigger) {
                // 아무 작업도 실행하지 않음
            }
        };
    }
}
