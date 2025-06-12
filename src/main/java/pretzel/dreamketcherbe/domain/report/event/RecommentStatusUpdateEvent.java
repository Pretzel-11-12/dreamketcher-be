package pretzel.dreamketcherbe.domain.report.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RecommentStatusUpdateEvent {

    private final Long reportId;
    private final Long recommentId;
    private final Long processedBy;
    private final LocalDateTime processedAt;

}
