package pretzel.dreamketcherbe.domain.notification.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pretzel.dreamketcherbe.domain.notification.entity.CommentNotificationType;
import pretzel.dreamketcherbe.domain.notification.entity.CommentRecOrNotRecNotification;
import pretzel.dreamketcherbe.domain.notification.entity.CommentReportNotification;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeLikeNotification;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeReportNotification;
import pretzel.dreamketcherbe.domain.notification.entity.RecommentReportNotification;

@Builder
public record NotificationResDto(
    Long notificationId,
    String type,
    String webtoonTitle,
    String episodeTitle,
    Integer episodeNo,
    Integer count,
    String message,
    LocalDateTime createdAt
) {

    public static NotificationResDto fromEpisodeLikeNotification(
        EpisodeLikeNotification episodeLikeNotification
    ) {
        return NotificationResDto.builder()
            .notificationId(episodeLikeNotification.getId())
            .type("EPISODE_LIKE")
            .webtoonTitle(episodeLikeNotification.getEpisode().getWebtoon().getTitle())
            .episodeTitle(episodeLikeNotification.getEpisode().getTitle())
            .episodeNo(episodeLikeNotification.getEpisode().getNo())
            .count(episodeLikeNotification.getLikeCount())
            .message(String.format(
                "에피소드 '%s'에 좋아요가 %d개 추가되었습니다.",
                episodeLikeNotification.getEpisode().getTitle(),
                episodeLikeNotification.getLikeCount()
            ))
            .build();
    }

    public static NotificationResDto fromEpisodeReportNotification(
        EpisodeReportNotification episodeReportNotification
    ) {
        String type = episodeReportNotificationType(episodeReportNotification);

        return NotificationResDto.builder()
            .notificationId(episodeReportNotification.getId())
            .type(type)
            .webtoonTitle(episodeReportNotification.getEpisode().getWebtoon().getTitle())
            .episodeTitle(episodeReportNotification.getEpisode().getTitle())
            .episodeNo(episodeReportNotification.getEpisode().getNo())
            .count(null)
            .message(String.format(
                "에피소드 '%s'에 신고가 접수되었습니다.",
                episodeReportNotification.getEpisode().getTitle()
            ))
            .build();
    }

    private static String episodeReportNotificationType(EpisodeReportNotification notification) {
        if (notification.getReporterId() != null) {
            return "EPISODE_REPORT_REPORTER";
        } else {
            return "EPISODE_REPORT_REPORTED";
        }
    }

    public static NotificationResDto fromCommentReportNotification(
        CommentReportNotification commentReportNotification
    ) {
        String type = commentReportNotificationType(commentReportNotification);

        return NotificationResDto.builder()
            .notificationId(commentReportNotification.getId())
            .type(type)
            .webtoonTitle(
                commentReportNotification.getComment().getEpisode().getWebtoon().getTitle())
            .episodeTitle(commentReportNotification.getComment().getEpisode().getTitle())
            .episodeNo(commentReportNotification.getComment().getEpisode().getNo())
            .count(null)
            .message(String.format(
                "댓글 '%s'에 신고가 접수되었습니다.",
                commentReportNotification.getComment().getContent()
            ))
            .build();
    }

    private static String commentReportNotificationType(CommentReportNotification notification) {
        if (notification.getReporterId() != null) {
            return "COMMENT_REPORT_REPORTER";
        } else {
            return "COMMENT_REPORT_REPORTED";
        }
    }

    public static NotificationResDto fromCommentRecOtNotRecNotification(
        CommentRecOrNotRecNotification commentRecOrNotRecNotification
    ) {
        String type = recOrNotRecType(commentRecOrNotRecNotification.getType());
        String action =
            commentRecOrNotRecNotification.getType() == CommentNotificationType.RECOMMENDATION
                ? "추천"
                : "비추천";

        return NotificationResDto.builder()
            .notificationId(commentRecOrNotRecNotification.getId())
            .type(type)
            .webtoonTitle(
                commentRecOrNotRecNotification.getComment().getEpisode().getWebtoon().getTitle())
            .episodeTitle(commentRecOrNotRecNotification.getComment().getEpisode().getTitle())
            .episodeNo(commentRecOrNotRecNotification.getComment().getEpisode().getNo())
            .count(null)
            .message(String.format(
                "댓글 '%s'에 %s이 추가되었습니다.",
                commentRecOrNotRecNotification.getComment().getContent(), action
            ))
            .build();
    }

    private static String recOrNotRecType(CommentNotificationType type) {
        if (type == CommentNotificationType.RECOMMENDATION) {
            return "COMMENT_RECOMMENDATION";
        } else {
            return "COMMENT_NOT_RECOMMENDATION";
        }
    }

    public static NotificationResDto fromRecommentReportNotification(
        RecommentReportNotification recommentReportNotification
    ) {
        String type = recommentReportNotificationType(recommentReportNotification);

        return NotificationResDto.builder()
            .notificationId(recommentReportNotification.getId())
            .type(type)
            .webtoonTitle(
                recommentReportNotification.getRecomment().getEpisode().getWebtoon().getTitle())
            .episodeTitle(recommentReportNotification.getRecomment().getEpisode().getTitle())
            .episodeNo(recommentReportNotification.getRecomment().getEpisode().getNo())
            .count(null)
            .message(String.format(
                "대댓글 '%s'에 신고가 접수되었습니다.",
                recommentReportNotification.getRecomment().getContent()
            ))
            .build();
    }

    private static String recommentReportNotificationType(
        RecommentReportNotification notification) {
        if (notification.getReporterId() != null) {
            return "RECOMMENT_REPORT_REPORTER";
        } else {
            return "RECOMMENT_REPORT_REPORTED";
        }
    }
}
