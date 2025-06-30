package pretzel.dreamketcherbe.domain.notification.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pretzel.dreamketcherbe.domain.notification.entity.RecommentNotificationType;

@Getter
@AllArgsConstructor
public class RecommentRecOrNotRecNotificationEvent {

    private final Long recommentId;
    private final Long memberId;
    private final Long commentId;
    private final Long episodeId;
    private final String episodeTitle;
    private final Integer episodeNumber;
    private final Long webtoonId;
    private final String webtoonTitle;
    private final RecommentNotificationType type;
    private final Integer count;
    private final LocalDateTime createdAt;

}
