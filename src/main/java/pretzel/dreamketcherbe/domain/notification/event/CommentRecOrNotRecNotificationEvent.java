package pretzel.dreamketcherbe.domain.notification.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pretzel.dreamketcherbe.domain.notification.entity.CommentNotificationType;

@Getter
@AllArgsConstructor
public class CommentRecOrNotRecNotificationEvent {

    private final Long commentId;
    private final Long memberId;
    private final Long episodeId;
    private final String episodeTitle;
    private final int episodeNumber;
    private final Long webtoonId;
    private final String webtoonTitle;
    private final CommentNotificationType type;
    private final int count;
    private final LocalDateTime createdAt;

}
