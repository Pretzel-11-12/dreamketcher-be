package pretzel.dreamketcherbe.common.logging;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class LatencyContext {

    private Long startTime;

    public void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    public Long getFullTime() {
        return System.currentTimeMillis() - startTime;
    }
}
