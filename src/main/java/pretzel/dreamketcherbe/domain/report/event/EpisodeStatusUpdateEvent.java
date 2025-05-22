package pretzel.dreamketcherbe.domain.report.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EpisodeStatusUpdateEvent {

    private final Long reportId;
    private final Long episodeId;
    private final Long processedBy;
    private final LocalDateTime processedAt;
}
