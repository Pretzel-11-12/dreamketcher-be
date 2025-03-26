package pretzel.dreamketcherbe.common.logging;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Component
@RequestScope
public class QueryCounter {

    private int queryCount;

    public void increaseQueryCount() {
        queryCount++;
    }
}
