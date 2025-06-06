package pretzel.dreamketcherbe.domain.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;
import pretzel.dreamketcherbe.domain.comment.exception.CommentException;
import pretzel.dreamketcherbe.domain.comment.exception.CommentExceptionType;
import pretzel.dreamketcherbe.domain.comment.repository.CommentRepository;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeException;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeExceptionType;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;
import pretzel.dreamketcherbe.domain.notification.entity.CommentNotificationType;
import pretzel.dreamketcherbe.domain.notification.entity.CommentRecOrNotRecNotification;
import pretzel.dreamketcherbe.domain.notification.entity.CommentReportNotification;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeLikeNotification;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeReportNotification;
import pretzel.dreamketcherbe.domain.notification.event.CommentRecOrNotRecNotificationEvent;
import pretzel.dreamketcherbe.domain.notification.event.EpisodeLikeNotificationEvent;
import pretzel.dreamketcherbe.domain.notification.repository.CommentRecOrNotRecNotificationRepository;
import pretzel.dreamketcherbe.domain.notification.repository.CommentReportNotificationRepository;
import pretzel.dreamketcherbe.domain.notification.repository.EpisodeLikeNotificationRepository;
import pretzel.dreamketcherbe.domain.notification.repository.EpisodeReportNotificationRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final EpisodeRepository episodeRepository;
    private final EpisodeLikeNotificationRepository episodeLikeNotificationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CommentRepository commentRepository;
    private final CommentRecOrNotRecNotificationRepository commentRecOrNotRecNotificationRepository;
    private final EpisodeReportNotificationRepository episodeReportNotificationRepository;
    private final CommentReportNotificationRepository commentReportNotificationRepository;

    /**
     * 에피소드 좋아요 알림 생성
     */
    public void likeNotification(Long episodeId, Long memberId) {
        Episode episode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        boolean isNotify = episode.incrementLikeCount();
        episodeRepository.save(episode);

        if (isNotify && !hasLikeNotification(episodeId, episode.getLikeCount())) {

            saveLikeNotification(episode);

            EpisodeLikeNotificationEvent event = new EpisodeLikeNotificationEvent(
                episode.getId(),
                episode.getMember().getId(),
                episode.getWebtoon().getTitle(),
                episode.getNo(),
                episode.getTitle(),
                episode.getLikeCount(),
                LocalDateTime.now()
            );
            eventPublisher.publishEvent(event);
        }
    }

    private boolean hasLikeNotification(Long episodeId, int likeCount) {
        return episodeLikeNotificationRepository.existsByEpisodeIdAndEpisodeLikeCount(episodeId,
            likeCount);
    }

    private void saveLikeNotification(Episode episode) {
        EpisodeLikeNotification notification = EpisodeLikeNotification.builder()
            .authorId(episode.getMember().getId())
            .episode(episode)
            .webtoonId(episode.getWebtoon().getId())
            .likeCount(episode.getLikeCount())
            .isRead(false)
            .expiredAt(LocalDateTime.now().plusDays(14))
            .build();

        episodeLikeNotificationRepository.save(notification);
    }

    /**
     * 에피소드 좋아요 전체 알림 조회
     */
    @Transactional(readOnly = true)
    public List<EpisodeLikeNotification> getAllLikeNotifications(Long memberId) {
        return episodeLikeNotificationRepository.findAllNotificationsByAuthorId(memberId,
            LocalDateTime.now());
    }

    /**
     * 에피소드 좋아요 읽지 않은 알림 조회
     */
    @Transactional(readOnly = true)
    public List<EpisodeLikeNotification> getUnreadLikeNotifications(Long memberId) {
        return episodeLikeNotificationRepository.findUnreadNotificationsByAuthorId(memberId,
            LocalDateTime.now());
    }

    /**
     * 에피소드 좋아요 읽지 않은 알림 갯수 조회
     */
    @Transactional(readOnly = true)
    public Long getUnreadLikeNotificationCount(Long memberId) {
        return episodeLikeNotificationRepository.countUnreadNotificationsByAuthorId(memberId,
            LocalDateTime.now());
    }

    /**
     * 에피소드 좋아요 알림 읽음 처리
     */
    public void markAsReadLikeNotification(Long notificationId) {
        EpisodeLikeNotification notification = episodeLikeNotificationRepository.findById(
                notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));

        notification.markAsRead();
        episodeLikeNotificationRepository.save(notification);
    }

    /**
     * 에피소드 좋아요 알림 만료 처리
     */
    public void cleanupExpiredLikeNotifications() {
        List<EpisodeLikeNotification> expiredNotifications = episodeLikeNotificationRepository.
            findExpiredNotifications(LocalDateTime.now());

        episodeLikeNotificationRepository.deleteAll(expiredNotifications);
        log.info("만료 알림 {}개 삭제 완료", expiredNotifications.size());
    }

    /**
     * 에피소드 신고 알림 - 전체 조회
     */
    @Transactional(readOnly = true)
    public List<EpisodeReportNotification> getAllReportNotifications(Long memberId) {
        return episodeReportNotificationRepository.findAllNotificationsByMemberId(memberId,
            LocalDateTime.now());
    }

    /**
     * 에피소드 신고 알림 - 읽지 않은 알림 조회
     */
    @Transactional(readOnly = true)
    public List<EpisodeReportNotification> getUnreadReportNotifications(Long memberId) {
        return episodeReportNotificationRepository.findUnreadNotificationsByMemberId(memberId,
            LocalDateTime.now());
    }

    /**
     * 에피소드 신고 알림 - 읽지 않은 알림 수
     */
    @Transactional(readOnly = true)
    public Long countUnreadReportNotifications(Long memberId) {
        return episodeReportNotificationRepository.countUnreadNotificationsByMemberId(memberId,
            LocalDateTime.now());
    }

    /**
     * 에피소드 신고 알림 - 읽음 처리
     */
    public void markAsReadEpisodeReportNotification(Long notificationId) {
        EpisodeReportNotification notification = episodeReportNotificationRepository.findById(
                notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));

        notification.markAsRead();
        episodeReportNotificationRepository.save(notification);
    }

    /**
     * 에피소드 신고 알림 - 만료된 알림 처리
     */
    public void cleanupExpiredEpisodeReportNotifications() {
        List<EpisodeReportNotification> expiredNotifications = episodeReportNotificationRepository.findExpiredNotifications(
            LocalDateTime.now());

        episodeReportNotificationRepository.deleteAll(expiredNotifications);
        log.info("만료된 에피소드 신고 알림 {}개 삭제 완료", expiredNotifications.size());
    }

    /**
     * 댓글 신고 알림 - 전체 조회
     */
    @Transactional(readOnly = true)
    public List<CommentReportNotification> getAllCommentReportNotifications(Long memberId) {
        return commentReportNotificationRepository.findAllCommentReportNotificationsByMemberId(
            memberId,
            LocalDateTime.now());
    }

    /**
     * 댓글 신고 알림 - 읽지 않은 알림 조회
     */
    @Transactional(readOnly = true)
    public List<CommentReportNotification> getUnreadCommentReportNotifications(Long memberId) {
        return commentReportNotificationRepository.findUnreadCommentReportNotificationsByMemberId(
            memberId, LocalDateTime.now());
    }

    /**
     * 댓글 신고 알림 - 읽지 않은 알림 수 조회
     */
    @Transactional(readOnly = true)
    public Long countUnreadCommentReportNotifications(Long memberId) {
        return commentReportNotificationRepository.countUnreadCommentReportNotificationsByMemberId(
            memberId, LocalDateTime.now());
    }

    /**
     * 댓글 신고 알림 - 읽음 처리
     */
    public void markAsReadCommentReportNotification(Long notificationId) {
        CommentReportNotification notification = commentReportNotificationRepository.findById(
                notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));

        notification.markAsRead();
        commentReportNotificationRepository.save(notification);
    }

    /**
     * 댓글 신고 알림 - 만료 처리
     */
    public void cleanupExpiredCommentReportNotifications() {
        List<CommentReportNotification> expiredNotifications = commentReportNotificationRepository
            .findExpiredCommentReportNotifications(LocalDateTime.now());

        commentReportNotificationRepository.deleteAll(expiredNotifications);
        log.info("만료된 댓글 신고 알림 {}개 삭제 완료", expiredNotifications.size());
    }

    /**
     * 댓글 추천 알림
     */
    public void recommendationNotification(Long commentId, int recommendationCount) {
        if (recommendationCount % 5 == 0 && recommendationCount > 0) {
            processNotification(commentId, CommentNotificationType.RECOMMENDATION,
                recommendationCount);
        }
    }

    /**
     * 댓글 비추천 알림
     */
    public void notRecommendationNotification(Long commentId, int notRecommendationCount) {
        if (notRecommendationCount % 5 == 0 && notRecommendationCount > 0) {
            processNotification(commentId, CommentNotificationType.NOT_RECOMMENDATION,
                notRecommendationCount);
        }
    }

    private void processNotification(Long commentId, CommentNotificationType type, int count) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        if (!hasRecOrNotRecNotification(commentId, type)) {
            saveRecOrNotRecNotification(comment, type, count);

            publisRecOrNotRecNotification(comment, type, count);

            log.info("댓글 {} 알림 생성 - 댓글: {}, 개수: {}",
                type == CommentNotificationType.RECOMMENDATION ? "추천" : "비추천",
                commentId, count);
        }
    }

    private boolean hasRecOrNotRecNotification(Long commentId, CommentNotificationType type) {
        return commentRecOrNotRecNotificationRepository.existsByCommentIdAndType(commentId, type);
    }

    private void saveRecOrNotRecNotification(Comment comment, CommentNotificationType type,
        int count) {
        CommentRecOrNotRecNotification notification;

        if (type == CommentNotificationType.RECOMMENDATION) {
            notification = CommentRecOrNotRecNotification.createForRecommendation(comment, count);
        } else {
            notification = CommentRecOrNotRecNotification.createForNotRecommendation(comment,
                count);
        }

        commentRecOrNotRecNotificationRepository.save(notification);
    }

    private void publisRecOrNotRecNotification(Comment comment, CommentNotificationType type,
        int count) {
        CommentRecOrNotRecNotificationEvent event = new CommentRecOrNotRecNotificationEvent(
            comment.getId(),
            comment.getMember().getId(),
            comment.getEpisode().getId(),
            comment.getEpisode().getTitle(),
            comment.getEpisode().getNo(),
            comment.getWebtoon().getId(),
            comment.getWebtoon().getTitle(),
            type,
            count,
            LocalDateTime.now()
        );
        eventPublisher.publishEvent(event);
    }

    /**
     * 모든 댓글 추천/비추천 알림 조회
     */
    @Transactional(readOnly = true)
    public List<CommentRecOrNotRecNotification> getAllCommentRecOrNotRecNotifications(
        Long memberId) {
        return commentRecOrNotRecNotificationRepository.findAllByMemberId(memberId,
            LocalDateTime.now());
    }

    /**
     * 읽지 않은 댓글 추천/비추천 알림 조회
     */
    @Transactional(readOnly = true)
    public List<CommentRecOrNotRecNotification> getUnreadCommentRecOrNotRecNotifications(
        Long memberId) {
        return commentRecOrNotRecNotificationRepository.findAllUnreadNotificationsByMemberId(
            memberId,
            LocalDateTime.now());
    }

    /**
     * 읽지 않은 알림 갯수 조회
     */
    @Transactional(readOnly = true)
    public Long getUnreadCommentRecOrNotRecNotificationCount(Long memberId) {
        return commentRecOrNotRecNotificationRepository.countUnreadNotificationsByMemberId(
            memberId, LocalDateTime.now());
    }

    /**
     * 알림 읽음 처리
     */
    public void markAsReadCommentRecOrNotRecNotification(Long notificationId) {
        CommentRecOrNotRecNotification notification = commentRecOrNotRecNotificationRepository
            .findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));

        notification.markAsRead();
        commentRecOrNotRecNotificationRepository.save(notification);
    }

    /**
     * 만료된 댓글 추천/비추천 알림 처리
     */
    public void cleanupExpiredCommentRecOrNotRecNotifications() {
        List<CommentRecOrNotRecNotification> expiredNotifications = commentRecOrNotRecNotificationRepository
            .findExpiredNotifications(LocalDateTime.now());

        commentRecOrNotRecNotificationRepository.deleteAll(expiredNotifications);
        log.info("만료된 댓글 추천/비추천 알림 {}개 삭제 완료", expiredNotifications.size());
    }
}
