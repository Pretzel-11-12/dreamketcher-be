package pretzel.dreamketcherbe.domain.notification.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pretzel.dreamketcherbe.domain.report.entity.ReportStatus;

@Getter
@AllArgsConstructor
public class RecommentReportNotificationEvent {

    private final ReportStatus type;
    private final Long reportId;
    private final Long reporterId;
    private final Long reportedMemberId;
    private final String episodeTitle;
    private final int episodeNumber;
    private final String webtoonTitle;
    private final String commentContent;
    private final Long recommentId;
    private final String adminNote;
    private final LocalDateTime createdAt;

}
