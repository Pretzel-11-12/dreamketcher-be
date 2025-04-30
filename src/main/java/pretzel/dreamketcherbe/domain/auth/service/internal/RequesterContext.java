package pretzel.dreamketcherbe.domain.auth.service.internal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import pretzel.dreamketcherbe.domain.auth.dto.RequestInfo;

@Component
@RequestScope
public class RequesterContext {

    @Getter
    @Setter
    private RequestInfo requester;
}
