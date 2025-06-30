package pretzel.dreamketcherbe.domain.notification.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.notification.dto.NotificationCountResDto;
import pretzel.dreamketcherbe.domain.notification.dto.NotificationReqDto;
import pretzel.dreamketcherbe.domain.notification.dto.NotificationResDto;
import pretzel.dreamketcherbe.domain.notification.entity.CommentRecOrNotRecNotification;
import pretzel.dreamketcherbe.domain.notification.entity.CommentReportNotification;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeLikeNotification;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeReportNotification;
import pretzel.dreamketcherbe.domain.notification.entity.RecommentRecOrNotRecNotification;
import pretzel.dreamketcherbe.domain.notification.entity.RecommentReportNotification;
import pretzel.dreamketcherbe.domain.notification.service.NotificationService;
import pretzel.dreamketcherbe.domain.notification.service.SSEService;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final SSEService SSEService;
    private final NotificationService notificationService;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@Auth Long memberId) {
        return SSEService.createConnection(memberId);
    }

    /**
     * 전체 알림 - 조회
     */
    @GetMapping
    public ResponseEntity<List<NotificationResDto>> getAllNotifications(@Auth Long memberId) {
        List<NotificationResDto> notifications = notificationService.getAllNotifications(memberId);

        return ResponseEntity.ok(notifications);
    }

    /**
     * 전체 알림 - 읽지 않은 알림 조회
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResDto>> getAllUnreadNotifications(@Auth Long memberId) {
        List<NotificationResDto> notifications = notificationService.getUnreadNotifications(
            memberId);

        return ResponseEntity.ok(notifications);
    }

    /**
     * 전체 알림 - 읽지 않은 알림 수 조회
     */
    @GetMapping("/unread/count")
    public ResponseEntity<NotificationCountResDto> getUnreadNotificationCount(@Auth Long memberId) {
        Long count = notificationService.getUnreadNotificationCount(memberId);
        return ResponseEntity.ok(new NotificationCountResDto(count));
    }

    /**
     * 전체 알림 - 모두 읽음 처리
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> readAllNotifications(@Auth Long memberId,
        @RequestBody NotificationReqDto request) {
        notificationService.markAsReadAllNotifications(request.notifications());

        return ResponseEntity.ok().build();
    }

    /**
     * 전체 알림 - 모두 삭제
     */
    @DeleteMapping("/delete-all")
    public ResponseEntity<Void> deleteAllNotifications(@Auth Long memberId,
        @RequestBody NotificationReqDto request) {
        notificationService.deleteAllNotifications(request.notifications());

        return ResponseEntity.ok().build();
    }

    /**
     * 에피소드 좋아요 알림 생성
     */
    @PostMapping("/episodes/{episodeId}/like")
    public ResponseEntity<Void> episodeLikeNotification(@Auth Long memberId,
        @PathVariable Long episodeId) {
        notificationService.likeNotification(episodeId, memberId);
        return ResponseEntity.ok().build();
    }

    /**
     * 에피소드 좋아요 알림 - 전체 조회
     */
    @GetMapping("/episodes/likes")
    public ResponseEntity<List<NotificationResDto>> getAllEpisodeLikeNotifications(
        @Auth Long memberId) {
        List<EpisodeLikeNotification> notifications = notificationService.getAllLikeNotifications(
            memberId);
        List<NotificationResDto> response = notifications.stream()
            .map(NotificationResDto::fromEpisodeLikeNotification)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 에피소드 좋아요 알림 - 읽지 않은 알림 조회
     */
    @GetMapping("/episodes/likes/unread")
    public ResponseEntity<List<NotificationResDto>> getUnreadEpisodeLikeNotifications(
        @Auth Long memberId) {
        List<EpisodeLikeNotification> notifications = notificationService.getUnreadLikeNotifications(
            memberId);
        List<NotificationResDto> response = notifications.stream()
            .map(NotificationResDto::fromEpisodeLikeNotification)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 에피소드 좋아요 알림 - 읽음 처리
     */
    @PatchMapping("/episodes/likes/{notificationId}/read")
    public ResponseEntity<Void> readEpisodeLikeNotification(
        @Auth Long memberId, @PathVariable Long notificationId) {

        notificationService.markAsReadLikeNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * 에피소드 신고 알림 - 전체 조회
     */
    @GetMapping("/episodes/reports")
    public ResponseEntity<List<NotificationResDto>> getAllEpisodeReportNotifications(
        @Auth Long memberId) {
        List<EpisodeReportNotification> notifications = notificationService.getAllReportNotifications(
            memberId);

        List<NotificationResDto> response = notifications.stream()
            .map(NotificationResDto::fromEpisodeReportNotification)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 에피소드 신고 알림 - 읽지 않은 알림 조회
     */
    @GetMapping("/episodes/reports/unread")
    public ResponseEntity<List<NotificationResDto>> getUnreadEpisodeReportNotifications(
        @Auth Long memberId) {
        List<EpisodeReportNotification> notifications = notificationService.getUnreadReportNotifications(
            memberId);

        List<NotificationResDto> response = notifications.stream()
            .map(NotificationResDto::fromEpisodeReportNotification)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 에피소드 신고 알림 - 읽지 않은 알림 수 조회
     */
    @GetMapping("/episodes/reports/unread/count")
    public ResponseEntity<NotificationCountResDto> getUnreadEpisodeReportNotificationCount(
        @Auth Long memberId) {
        Long count = notificationService.countUnreadReportNotifications(memberId);
        return ResponseEntity.ok(new NotificationCountResDto(count));
    }

    /**
     * 에피소드 신고 알림 - 읽음 처리
     */
    @PatchMapping("/episodes/reports/{notificationId}/read")
    public ResponseEntity<Void> readEpisodeReportNotification(@Auth Long memberId,
        @PathVariable Long notificationId) {
        notificationService.markAsReadEpisodeReportNotification(notificationId);

        return ResponseEntity.ok().build();
    }

    /**
     * 댓글 신고 알림 - 전체 조회
     */
    @GetMapping("/comments/reports")
    public ResponseEntity<List<NotificationResDto>> getAllCommentReportNotifications(
        @Auth Long memberId) {
        List<CommentReportNotification> notifications = notificationService.getAllCommentReportNotifications(
            memberId);

        List<NotificationResDto> response = notifications.stream()
            .map(NotificationResDto::fromCommentReportNotification)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 신고 알림 - 읽지 않은 알림 조회
     */
    @GetMapping("/comments/reports/unread")
    public ResponseEntity<List<NotificationResDto>> getUnreadCommentReportNotifications(
        @Auth Long memberId) {
        List<CommentReportNotification> notifications = notificationService.getUnreadCommentReportNotifications(
            memberId);

        List<NotificationResDto> response = notifications.stream()
            .map(NotificationResDto::fromCommentReportNotification)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 신고 알림 - 읽지 않은 알림 수 조회
     */
    @GetMapping("/comments/reports/unread/count")
    public ResponseEntity<NotificationCountResDto> getUnreadCommentReportNotificationCount(
        @Auth Long memberId) {
        Long count = notificationService.countUnreadCommentReportNotifications(memberId);

        return ResponseEntity.ok(new NotificationCountResDto(count));
    }

    /**
     * 댓글 신고 알림 - 읽음 처리
     */
    @PatchMapping("/comments/reports/{notificationId}/read")
    public ResponseEntity<Void> readCommentReportNotification(@Auth Long memberId,
        @PathVariable Long notificationId) {
        notificationService.markAsReadCommentReportNotification(notificationId);

        return ResponseEntity.ok().build();
    }

    /**
     * 댓글 추천 알림
     */
    @PostMapping("/comments/{commentId}/recommend-notification")
    public ResponseEntity<Void> commentRecommendNotification(@Auth Long memberId,
        @PathVariable Long commentId, @RequestParam int recommendCount) {
        notificationService.recommendationNotification(commentId, recommendCount);

        return ResponseEntity.ok().build();
    }

    /**
     * 댓글 비추천 알림
     */
    @PostMapping("/comments/{commentId}/notrecommend-notification")
    public ResponseEntity<Void> commentNotRecommendNotification(@Auth Long memberId,
        @PathVariable Long commentId, @RequestParam int notRecommendCount) {
        notificationService.notRecommendationNotification(commentId, notRecommendCount);

        return ResponseEntity.ok().build();
    }

    /**
     * 댓글 추천/비추천 알림 - 전체 조회
     */
    @GetMapping("/comments/recommendations")
    public ResponseEntity<List<NotificationResDto>> getAllCommentRecommendationNotifications(
        @Auth Long memberId) {
        List<CommentRecOrNotRecNotification> notifications = notificationService.getAllCommentRecOrNotRecNotifications(
            memberId);

        List<NotificationResDto> response = notifications.stream()
            .map(NotificationResDto::fromCommentRecOtNotRecNotification)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 추천/비추천 알림 - 읽지 않은 알림 조회
     */
    @GetMapping("/comments/recommendations/unread")
    public ResponseEntity<List<NotificationResDto>> getUnreadCommentRecommendationNotifications(
        @Auth Long memberId) {
        List<CommentRecOrNotRecNotification> notifications = notificationService.getUnreadCommentRecOrNotRecNotifications(
            memberId);

        List<NotificationResDto> response = notifications.stream()
            .map(NotificationResDto::fromCommentRecOtNotRecNotification)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 추천/비추천 알림 - 읽지 않은 알림 수 조회
     */
    @GetMapping("/comments/recommendations/unread/count")
    public ResponseEntity<NotificationCountResDto> getUnreadCommentRecommendationNotificationCount(
        @Auth Long memberId) {
        Long count = notificationService.getUnreadCommentRecOrNotRecNotificationCount(memberId);

        return ResponseEntity.ok(new NotificationCountResDto(count));
    }

    /**
     * 댓글 추천/비추천 알림 - 읽음 처리
     */
    @PatchMapping("/comments/recommendations/{notificationId}/read")
    public ResponseEntity<Void> readCommentRecommendationNotification(@Auth Long memberId,
        @PathVariable Long notificationId) {
        notificationService.markAsReadCommentRecOrNotRecNotification(notificationId);

        return ResponseEntity.ok().build();
    }

    /**
     * 답글 신고 알림 - 전체 조회
     */
    @GetMapping("/recomments/reports")
    public ResponseEntity<List<NotificationResDto>> getAllRecommentReportNotifications(
        @Auth Long memberId) {
        List<RecommentReportNotification> notifications = notificationService.getAllRecommentReportNotifications(
            memberId);

        List<NotificationResDto> response = notifications.stream()
            .map(NotificationResDto::fromRecommentReportNotification)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 답글 신고 알림 - 읽지 않은 알림 조회
     */
    @GetMapping("/recomments/reports/unread")
    public ResponseEntity<List<NotificationResDto>> getUnreadRecommentReportNotifications(
        @Auth Long memberId) {
        List<RecommentReportNotification> notifications = notificationService.getUnreadRecommentReportNotifications(
            memberId);

        List<NotificationResDto> response = notifications.stream()
            .map(NotificationResDto::fromRecommentReportNotification)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 답글 신고 알림 - 읽지 않은 알림 수 조회
     */
    @GetMapping("/recomments/reports/unread/count")
    public ResponseEntity<NotificationCountResDto> getUnreadRecommentReportNotificationCount(
        @Auth Long memberId) {
        Long count = notificationService.countUnreadRecommentReportNotifications(memberId);

        return ResponseEntity.ok(new NotificationCountResDto(count));
    }

    /**
     * 답글 신고 알림 - 읽음 처리
     */
    @PatchMapping("/recomments/reports/{notificationId}/read")
    public ResponseEntity<Void> readRecommentReportNotification(@Auth Long memberId,
        @PathVariable Long notificationId) {
        notificationService.markAsReadRecommentReportNotification(notificationId);

        return ResponseEntity.ok().build();
    }

    /**
     * 대댓글 추천 알림
     */
    @PostMapping("/recomments/{recommentId}/recommend-notification")
    public ResponseEntity<Void> recommentRecommendNotification(@Auth Long memberId,
        @PathVariable Long recommentId, @RequestParam int recommendCount) {
        notificationService.recommentRecommendationNotification(recommentId, recommendCount);

        return ResponseEntity.ok().build();
    }

    /**
     * 대댓글 비추천 알림
     */
    @PostMapping("/recomments/{recommentId}/notrecommend-notification")
    public ResponseEntity<Void> recommentNotRecommendNotification(@Auth Long memberId,
        @PathVariable Long recommentId, @RequestParam int notRecommendCount) {
        notificationService.recommentNotRecommendationNotification(recommentId, notRecommendCount);

        return ResponseEntity.ok().build();
    }

    /**
     * 대댓글 추천/비추천 알림 - 전체 조회
     */
    @GetMapping("/recomments/recommendations")
    public ResponseEntity<List<NotificationResDto>> getAllRecommentRecommendationNotifications(
        @Auth Long memberId) {
        List<RecommentRecOrNotRecNotification> notifications = notificationService.getAllRecommentRecOrNotRecNotifications(
            memberId);

        List<NotificationResDto> response = notifications.stream()
            .map(NotificationResDto::fromRecommentRecOrNotRecNotification)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 대댓글 추천/비추천 알림 - 읽지 않은 알림 조회
     */
    @GetMapping("/recomments/recommendations/unread")
    public ResponseEntity<List<NotificationResDto>> getUnreadRecommentRecommendationNotifications(
        @Auth Long memberId) {
        List<RecommentRecOrNotRecNotification> notifications = notificationService.getUnreadRecommentRecOrNotRecNotifications(
            memberId);

        List<NotificationResDto> response = notifications.stream()
            .map(NotificationResDto::fromRecommentRecOrNotRecNotification)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 대댓글 추천/비추천 알림 - 읽지 않은 알림 수 조회
     */
    @GetMapping("/recomments/recommendations/unread/count")
    public ResponseEntity<NotificationCountResDto> getUnreadRecommentRecommendationNotificationCount(
        @Auth Long memberId) {
        Long count = notificationService.getUnreadRecommentRecOrNotRecNotificationCount(memberId);

        return ResponseEntity.ok(new NotificationCountResDto(count));
    }

    /**
     * 대댓글 추천/비추천 알림 - 읽음 처리
     */
    @PatchMapping("/recomments/recommendations/{notificationId}/read")
    public ResponseEntity<Void> readRecommentRecommendationNotification(@Auth Long memberId,
        @PathVariable Long notificationId) {
        notificationService.markAsReadRecommentRecOrNotRecNotification(notificationId);

        return ResponseEntity.ok().build();
    }
}
