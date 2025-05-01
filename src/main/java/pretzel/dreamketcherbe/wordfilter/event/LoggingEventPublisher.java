package pretzel.dreamketcherbe.wordfilter.event;

import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoggingEventPublisher implements AdminEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(Object event) {
        LocalDateTime timesteamp = LocalDateTime.now();
        System.out.println("이벤트 발행" + event.getClass().getSimpleName() + ": " + timesteamp);

        Arrays.stream(event.getClass().getDeclaredFields())
            .peek(f -> f.setAccessible(true))
            .forEach(f -> {
                try {
                    System.out.println("발행 내용" + f.getName() + ": " + f.get(event));
                } catch (IllegalAccessException e) {
                    System.out.println("발행 내용" + f.getName() + ": " + e.getMessage());
                }
            });
        eventPublisher.publishEvent(event);
    }

}
