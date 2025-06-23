package pretzel.dreamketcherbe.domain.notification.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EpisodeLikeNotificationEvent {

    private final Long episodeId;
    private final long memberId; // 작가
    private final String webtoonTitle;
    private final int episodeNumber;
    private final String episodeTitle;
    private final int currentLikeCount;
    private final LocalDateTime createdAt;
}
