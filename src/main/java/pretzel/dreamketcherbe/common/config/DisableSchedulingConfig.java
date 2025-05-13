package pretzel.dreamketcherbe.common.config;

import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
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
            public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
                // 아무 작업도 실행하지 않고, 이미 완료된 것처럼 동작하는 ScheduledFuture 반환
                return new NoOpScheduledFuture();
            }
        };
    }

    // 아무 작업도 수행하지 않는 ScheduledFuture 구현체
    private static class NoOpScheduledFuture implements ScheduledFuture<Object> {

        @Override
        public long getDelay(TimeUnit unit) {
            return 0;
        }

        @Override
        public int compareTo(Delayed other) {
            return 0;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return true;
        }

        @Override
        public boolean isCancelled() {
            return true;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public Object get() {
            return null;
        }

        @Override
        public Object get(long timeout, TimeUnit unit) {
            return null;
        }
    }
}