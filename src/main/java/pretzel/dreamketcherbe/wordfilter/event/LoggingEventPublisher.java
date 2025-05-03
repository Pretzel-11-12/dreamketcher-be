package pretzel.dreamketcherbe.wordfilter.event;

import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingEventPublisher implements AdminEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(Object event) {
        LocalDateTime timestamp = LocalDateTime.now();
        log.info("이벤트 발행{}: {}", event.getClass().getSimpleName(), timestamp);

        Arrays.stream(event.getClass().getDeclaredFields())
            .forEach(f -> {
                boolean accessible = f.canAccess(event);
                try {
                    f.setAccessible(true);
                    System.out.println("발행 내용 " + f.getName() + ": " + f.get(event));
                } catch (IllegalAccessException e) {
                    System.out.println("발행 내용 " + f.getName() + ": " + e.getMessage());
                } finally {
                    f.setAccessible(accessible);
                }
            });
        eventPublisher.publishEvent(event);
    }
}
