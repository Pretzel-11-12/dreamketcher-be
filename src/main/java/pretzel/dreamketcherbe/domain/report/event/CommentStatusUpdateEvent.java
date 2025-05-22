package pretzel.dreamketcherbe.domain.report.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentStatusUpdateEvent {

    private final Long reportId;
    private final Long commentId;
    private final Long processedBy;
    private final LocalDateTime processedAt;

}
