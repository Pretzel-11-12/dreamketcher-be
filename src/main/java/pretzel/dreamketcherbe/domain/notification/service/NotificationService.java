package pretzel.dreamketcherbe.domain.notification.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;
import pretzel.dreamketcherbe.domain.comment.exception.CommentException;
import pretzel.dreamketcherbe.domain.comment.exception.CommentExceptionType;
import pretzel.dreamketcherbe.domain.comment.repository.CommentRepository;
import pretzel.dreamketcherbe.domain.comment.repository.RecommentRepository;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeException;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeExceptionType;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;
import pretzel.dreamketcherbe.domain.notification.dto.NotificationReqDto.NotificationItem;
import pretzel.dreamketcherbe.domain.notification.dto.NotificationResDto;
import pretzel.dreamketcherbe.domain.notification.entity.CommentNotificationType;
import pretzel.dreamketcherbe.domain.notification.entity.CommentRecOrNotRecNotification;
import pretzel.dreamketcherbe.domain.notification.entity.CommentReportNotification;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeLikeNotification;
import pretzel.dreamketcherbe.domain.notification.entity.EpisodeReportNotification;
import pretzel.dreamketcherbe.domain.notification.entity.RecommentNotificationType;
import pretzel.dreamketcherbe.domain.notification.entity.RecommentRecOrNotRecNotification;
import pretzel.dreamketcherbe.domain.notification.entity.RecommentReportNotification;
import pretzel.dreamketcherbe.domain.notification.event.CommentRecOrNotRecNotificationEvent;
import pretzel.dreamketcherbe.domain.notification.event.EpisodeLikeNotificationEvent;
import pretzel.dreamketcherbe.domain.notification.event.RecommentRecOrNotRecNotificationEvent;
import pretzel.dreamketcherbe.domain.notification.repository.CommentRecOrNotRecNotificationRepository;
import pretzel.dreamketcherbe.domain.notification.repository.CommentReportNotificationRepository;
import pretzel.dreamketcherbe.domain.notification.repository.EpisodeLikeNotificationRepository;
import pretzel.dreamketcherbe.domain.notification.repository.EpisodeReportNotificationRepository;
import pretzel.dreamketcherbe.domain.notification.repository.RecommentRecOrNotRecNotificationRepository;
import pretzel.dreamketcherbe.domain.notification.repository.RecommentReportNotificationRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private static final int NOTIFICATION_EXPIRE_DAYS = 14;
    private static final int RECOMMENDATION_THRESHOLD = 5;

    private final EpisodeRepository episodeRepository;
    private final EpisodeLikeNotificationRepository episodeLikeNotificationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CommentRepository commentRepository;
    private final CommentRecOrNotRecNotificationRepository commentRecOrNotRecNotificationRepository;
    private final EpisodeReportNotificationRepository episodeReportNotificationRepository;
    private final CommentReportNotificationRepository commentReportNotificationRepository;
    private final RecommentReportNotificationRepository recommentReportNotificationRepository;
    private final RecommentRecOrNotRecNotificationRepository recommentRecOrNotRecNotificationRepository;
    private final RecommentRepository recommentRepository;

    /**
     * 모든 알림 - 전체 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationResDto> getAllNotifications(Long memberId) {
        return collectAllNotifications(memberId, false);
    }

    /**
     * 모든 알림 - 읽지 않은 알림 전체 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationResDto> getUnreadNotifications(Long memberId) {
        return collectAllNotifications(memberId, true);
    }

    /**
     * 모든 알림 - 읽지 않은 알림 수
     */
    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount(Long memberId) {
        // repository 직접 호출로 self-invocation 방지
        Long likeCount = episodeLikeNotificationRepository.countUnreadNotificationsByAuthorId(
            memberId, LocalDateTime.now());
        Long episodeReportCount = episodeReportNotificationRepository.countUnreadNotificationsByMemberId(
            memberId, LocalDateTime.now());
        Long commentReportCount = commentReportNotificationRepository.countUnreadCommentReportNotificationsByMemberId(
            memberId, LocalDateTime.now());
        Long commentRecOrNotRecCount = commentRecOrNotRecNotificationRepository.countUnreadNotificationsByMemberId(
            memberId, LocalDateTime.now());
        Long recommentReportCount = recommentReportNotificationRepository.countUnreadRecommentReportNotificationsByMemberId(
            memberId, LocalDateTime.now());
        Long recommentRecOrNotRecCount = recommentRecOrNotRecNotificationRepository.countUnreadNotificationsByMemberId(
            memberId, LocalDateTime.now());

        return likeCount + episodeReportCount + commentReportCount + commentRecOrNotRecCount
            + recommentReportCount + recommentRecOrNotRecCount;
    }

    /**
     * 알림 수집 헬퍼 메서드
     */
    private List<NotificationResDto> collectAllNotifications(Long memberId, boolean unreadOnly) {

        List<EpisodeLikeNotification> likeNotifications = unreadOnly
            ? episodeLikeNotificationRepository.findUnreadNotificationsByAuthorId(memberId,
            LocalDateTime.now())
            : episodeLikeNotificationRepository.findAllNotificationsByAuthorId(memberId,
                LocalDateTime.now());
        List<NotificationResDto> notifications = new ArrayList<>(likeNotifications.stream()
            .map(NotificationResDto::fromEpisodeLikeNotification)
            .toList());

        List<EpisodeReportNotification> episodeReportNotifications = unreadOnly
            ? episodeReportNotificationRepository.findUnreadNotificationsByMemberId(memberId,
            LocalDateTime.now())
            : episodeReportNotificationRepository.findAllNotificationsByMemberId(memberId,
                LocalDateTime.now());
        notifications.addAll(episodeReportNotifications.stream()
            .map(NotificationResDto::fromEpisodeReportNotification)
            .toList());

        List<CommentReportNotification> commentReportNotifications = unreadOnly
            ? commentReportNotificationRepository.findUnreadCommentReportNotificationsByMemberId(
            memberId, LocalDateTime.now())
            : commentReportNotificationRepository.findAllCommentReportNotificationsByMemberId(
                memberId, LocalDateTime.now());
        notifications.addAll(commentReportNotifications.stream()
            .map(NotificationResDto::fromCommentReportNotification)
            .toList());

        List<CommentRecOrNotRecNotification> commentRecOrNotRecNotifications = unreadOnly
            ? commentRecOrNotRecNotificationRepository.findAllUnreadNotificationsByMemberId(
            memberId, LocalDateTime.now())
            : commentRecOrNotRecNotificationRepository.findAllByMemberId(memberId,
                LocalDateTime.now());
        notifications.addAll(commentRecOrNotRecNotifications.stream()
            .map(NotificationResDto::fromCommentRecOtNotRecNotification)
            .toList());

        List<RecommentReportNotification> recommentReportNotifications = unreadOnly
            ? recommentReportNotificationRepository.findUnreadRecommentReportNotificationsByMemberId(
            memberId, LocalDateTime.now())
            : recommentReportNotificationRepository.findAllRecommentReportNotificationsByMemberId(
                memberId, LocalDateTime.now());
        notifications.addAll(recommentReportNotifications.stream()
            .map(NotificationResDto::fromRecommentReportNotification)
            .toList());

        List<RecommentRecOrNotRecNotification> recommentRecOrNotRecNotifications = unreadOnly
            ? recommentRecOrNotRecNotificationRepository.findAllUnreadNotificationsByMemberId(
            memberId, LocalDateTime.now())
            : recommentRecOrNotRecNotificationRepository.findAllByMemberId(memberId,
                LocalDateTime.now());
        notifications.addAll(recommentRecOrNotRecNotifications.stream()
            .map(NotificationResDto::fromRecommentRecOrNotRecNotification)
            .toList());

        notifications.sort((n1, n2) -> n2.createdAt().compareTo(n1.createdAt()));

        return notifications;
    }

    /**
     * 모든 알림 - 전체 읽음 처리
     */
    @Transactional
    public void markAsReadAllNotifications(List<NotificationItem> notificationItems) {
        for (NotificationItem notification : notificationItems) {
            switch (notification.type()) {
                case "EPISODE_LIKE":
                    markAsReadLikeNotification(notification.notificationId());
                    break;
                case "EPISODE_REPORT_REPORTER":
                case "EPISODE_REPORT_REPORTED":
                    markAsReadEpisodeReportNotification(notification.notificationId());
                    break;
                case "COMMENT_REPORT":
                    markAsReadCommentReportNotification(notification.notificationId());
                    break;
                case "COMMENT_RECOMMENDATION":
                case "COMMENT_NOT_RECOMMENDATION":
                    markAsReadCommentRecOrNotRecNotification(notification.notificationId());
                    break;
                case "RECOMMENT_REPORT":
                    markAsReadRecommentReportNotification(notification.notificationId());
                    break;
                case "RECOMMENT_RECOMMENDATION":
                case "RECOMMENT_NOT_RECOMMENDATION":
                    markAsReadRecommentRecOrNotRecNotification(notification.notificationId());
                    break;
                default:
                    log.warn("알 수 없는 알림 타입: {}", notification.type());
            }
        }
    }

    /**
     * 모든 알림 - 전체 삭제
     */
    @Transactional
    public void deleteAllNotifications(List<NotificationItem> notificationItems) {
        for (NotificationItem notification : notificationItems) {
            switch (notification.type()) {
                case "EPISODE_LIKE":
                    episodeLikeNotificationRepository.deleteById(notification.notificationId());
                    break;
                case "EPISODE_REPORT":
                    episodeReportNotificationRepository.deleteById(notification.notificationId());
                    break;
                case "COMMENT_REPORT":
                    commentReportNotificationRepository.deleteById(notification.notificationId());
                    break;
                case "COMMENT_RECOMMENDATION":
                case "COMMENT_NOT_RECOMMENDATION":
                    commentRecOrNotRecNotificationRepository.deleteById(
                        notification.notificationId());
                    break;
                case "RECOMMENT_REPORT":
                    recommentReportNotificationRepository.deleteById(notification.notificationId());
                    break;
                case "RECOMMENT_RECOMMENDATION":
                case "RECOMMENT_NOT_RECOMMENDATION":
                    recommentRecOrNotRecNotificationRepository.deleteById(
                        notification.notificationId());
                    break;

                default:
                    log.warn("알 수 없는 알림 타입: {}", notification.type());
            }
        }
    }

    /**
     * 에피소드 좋아요 알림 생성
     */
    @Transactional
    public void likeNotification(Long episodeId, Long memberId) {
        // 1. 핵심 비즈니스 로직: 좋아요 수 증가
        Episode episode = incrementEpisodeLike(episodeId);

        // 2. 알림 생성 조건 확인 및 처리 (별도 트랜잭션)
        if (shouldCreateLikeNotification(episode)) {
            createLikeNotificationAsync(episode);
        }
    }

    /**
     * 에피소드 좋아요 수 증가
     */
    private Episode incrementEpisodeLike(Long episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        boolean isNotify = episode.incrementLikeCount();
        if (!isNotify) {
            log.debug("좋아요 알림 조건 미충족 - 에피소드: {}, 현재 좋아요: {}",
                episodeId, episode.getLikeCount());
        }

        return episodeRepository.save(episode);
    }

    /**
     * 알림 생성 조건 확인
     */
    private boolean shouldCreateLikeNotification(Episode episode) {
        return !hasLikeNotification(episode.getId(), episode.getLikeCount());
    }

    /**
     * 좋아요 알림 생성
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createLikeNotificationAsync(Episode episode) {
        try {
            saveLikeNotification(episode);
            publishLikeNotificationEvent(episode);

            log.info("좋아요 알림 생성 성공 - 에피소드: {}, 좋아요: {}",
                episode.getId(), episode.getLikeCount());
        } catch (Exception e) {
            log.error("좋아요 알림 생성 실패 - 에피소드: {}, 에러: {}",
                episode.getId(), e.getMessage(), e);
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
            .expiredAt(LocalDateTime.now().plusDays(NOTIFICATION_EXPIRE_DAYS))
            .build();

        episodeLikeNotificationRepository.save(notification);
    }

    /**
     * 이벤트 발행
     */
    private void publishLikeNotificationEvent(Episode episode) {
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

    @Transactional(readOnly = true)
    public List<EpisodeLikeNotification> getAllLikeNotifications(Long memberId) {
        return episodeLikeNotificationRepository.findAllNotificationsByAuthorId(memberId,
            LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<EpisodeLikeNotification> getUnreadLikeNotifications(Long memberId) {
        return episodeLikeNotificationRepository.findUnreadNotificationsByAuthorId(memberId,
            LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Long getUnreadLikeNotificationCount(Long memberId) {
        return episodeLikeNotificationRepository.countUnreadNotificationsByAuthorId(memberId,
            LocalDateTime.now());
    }

    /**
     * 댓글 추천 알림
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recommendationNotification(Long commentId, int recommendationCount) {
        if (recommendationCount % RECOMMENDATION_THRESHOLD == 0 && recommendationCount > 0) {
            processCommentNotification(commentId, CommentNotificationType.RECOMMENDATION,
                recommendationCount);
        }
    }

    /**
     * 댓글 비추천 알림
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notRecommendationNotification(Long commentId, int notRecommendationCount) {
        if (notRecommendationCount % RECOMMENDATION_THRESHOLD == 0 && notRecommendationCount > 0) {
            processCommentNotification(commentId, CommentNotificationType.NOT_RECOMMENDATION,
                notRecommendationCount);
        }
    }

    /**
     * 댓글 알림 처리
     */
    private void processCommentNotification(Long commentId, CommentNotificationType type,
        int count) {
        try {
            Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

            if (!hasRecOrNotRecNotification(commentId, type)) {
                saveRecOrNotRecNotification(comment, type, count);
                publishCommentNotificationEvent(comment, type, count);

                log.info("댓글 {} 알림 생성 성공 - 댓글: {}, 개수: {}",
                    type == CommentNotificationType.RECOMMENDATION ? "추천" : "비추천",
                    commentId, count);
            }
        } catch (Exception e) {
            log.error("댓글 알림 생성 실패 - 댓글: {}, 타입: {}, 에러: {}",
                commentId, type, e.getMessage(), e);
        }
    }

    private boolean hasRecOrNotRecNotification(Long commentId, CommentNotificationType type) {
        return commentRecOrNotRecNotificationRepository.existsByCommentIdAndType(commentId, type);
    }

    private void saveRecOrNotRecNotification(Comment comment, CommentNotificationType type,
        int count) {
        CommentRecOrNotRecNotification notification =
            (type == CommentNotificationType.RECOMMENDATION)
                ? CommentRecOrNotRecNotification.createForRecommendation(comment, count)
                : CommentRecOrNotRecNotification.createForNotRecommendation(comment, count);

        commentRecOrNotRecNotificationRepository.save(notification);
    }

    /**
     * 댓글 알림 이벤트 발행
     */
    private void publishCommentNotificationEvent(Comment comment, CommentNotificationType type,
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
     * 대댓글 추천 알림
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recommentRecommendationNotification(Long recommentId, int recommendationCount) {
        if (recommendationCount % RECOMMENDATION_THRESHOLD == 0 && recommendationCount > 0) {
            processRecommentNotification(recommentId, RecommentNotificationType.RECOMMENDATION,
                recommendationCount);
        }
    }

    /**
     * 대댓글 비추천 알림
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recommentNotRecommendationNotification(Long recommentId,
        int notRecommendationCount) {
        if (notRecommendationCount % RECOMMENDATION_THRESHOLD == 0 && notRecommendationCount > 0) {
            processRecommentNotification(recommentId, RecommentNotificationType.NOT_RECOMMENDATION,
                notRecommendationCount);
        }
    }

    /**
     * 대댓글 알림 처리
     */
    private void processRecommentNotification(Long recommentId, RecommentNotificationType type,
        int count) {
        try {
            Recomment recomment = recommentRepository.findById(recommentId)
                .orElseThrow(
                    () -> new CommentException(CommentExceptionType.RECOMMENT_NOT_FOUND));

            if (!hasRecommentRecOrNotRecNotification(recommentId, type)) {
                saveRecommentRecOrNotRecNotification(recomment, type, count);
                publishRecommentNotificationEvent(recomment, type, count);

                log.info("대댓글 {} 알림 생성 성공 - 대댓글: {}, 개수: {}",
                    type == RecommentNotificationType.RECOMMENDATION ? "추천" : "비추천",
                    recommentId, count);
            }
        } catch (Exception e) {
            log.error("대댓글 알림 생성 실패 - 대댓글: {}, 타입: {}, 에러: {}",
                recommentId, type, e.getMessage(), e);
        }
    }

    private boolean hasRecommentRecOrNotRecNotification(Long recommentId,
        RecommentNotificationType type) {
        return recommentRecOrNotRecNotificationRepository.existsByRecommentIdAndType(recommentId,
            type);
    }

    private void saveRecommentRecOrNotRecNotification(Recomment recomment,
        RecommentNotificationType type,
        int count) {
        RecommentRecOrNotRecNotification notification =
            (type == RecommentNotificationType.RECOMMENDATION)
                ? RecommentRecOrNotRecNotification.createForRecommendation(recomment, count)
                : RecommentRecOrNotRecNotification.createForNotRecommendation(recomment, count);

        recommentRecOrNotRecNotificationRepository.save(notification);
    }

    /**
     * 대댓글 알림 이벤트 발행
     */
    private void publishRecommentNotificationEvent(Recomment recomment,
        RecommentNotificationType type,
        int count) {
        RecommentRecOrNotRecNotificationEvent event = new RecommentRecOrNotRecNotificationEvent(
            recomment.getId(),
            recomment.getMember().getId(),
            recomment.getComment().getId(),
            recomment.getComment().getEpisode().getId(),
            recomment.getComment().getEpisode().getTitle(),
            recomment.getComment().getEpisode().getNo(),
            recomment.getComment().getWebtoon().getId(),
            recomment.getComment().getWebtoon().getTitle(),
            type,
            count,
            LocalDateTime.now()
        );
        eventPublisher.publishEvent(event);
    }

    @Transactional(readOnly = true)
    public List<EpisodeReportNotification> getAllReportNotifications(Long memberId) {
        return episodeReportNotificationRepository.findAllNotificationsByMemberId(memberId,
            LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<EpisodeReportNotification> getUnreadReportNotifications(Long memberId) {
        return episodeReportNotificationRepository.findUnreadNotificationsByMemberId(memberId,
            LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Long countUnreadReportNotifications(Long memberId) {
        return episodeReportNotificationRepository.countUnreadNotificationsByMemberId(memberId,
            LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<CommentReportNotification> getAllCommentReportNotifications(Long memberId) {
        return commentReportNotificationRepository.findAllCommentReportNotificationsByMemberId(
            memberId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<CommentReportNotification> getUnreadCommentReportNotifications(Long memberId) {
        return commentReportNotificationRepository.findUnreadCommentReportNotificationsByMemberId(
            memberId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Long countUnreadCommentReportNotifications(Long memberId) {
        return commentReportNotificationRepository.countUnreadCommentReportNotificationsByMemberId(
            memberId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<CommentRecOrNotRecNotification> getAllCommentRecOrNotRecNotifications(
        Long memberId) {
        return commentRecOrNotRecNotificationRepository.findAllByMemberId(memberId,
            LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<CommentRecOrNotRecNotification> getUnreadCommentRecOrNotRecNotifications(
        Long memberId) {
        return commentRecOrNotRecNotificationRepository.findAllUnreadNotificationsByMemberId(
            memberId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Long getUnreadCommentRecOrNotRecNotificationCount(Long memberId) {
        return commentRecOrNotRecNotificationRepository.countUnreadNotificationsByMemberId(memberId,
            LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<RecommentReportNotification> getAllRecommentReportNotifications(Long memberId) {
        return recommentReportNotificationRepository.findAllRecommentReportNotificationsByMemberId(
            memberId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<RecommentReportNotification> getUnreadRecommentReportNotifications(Long memberId) {
        return recommentReportNotificationRepository.findUnreadRecommentReportNotificationsByMemberId(
            memberId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Long countUnreadRecommentReportNotifications(Long memberId) {
        return recommentReportNotificationRepository.countUnreadRecommentReportNotificationsByMemberId(
            memberId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<RecommentRecOrNotRecNotification> getAllRecommentRecOrNotRecNotifications(
        Long memberId) {
        return recommentRecOrNotRecNotificationRepository.findAllByMemberId(memberId,
            LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<RecommentRecOrNotRecNotification> getUnreadRecommentRecOrNotRecNotifications(
        Long memberId) {
        return recommentRecOrNotRecNotificationRepository.findAllUnreadNotificationsByMemberId(
            memberId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Long getUnreadRecommentRecOrNotRecNotificationCount(Long memberId) {
        return recommentRecOrNotRecNotificationRepository.countUnreadNotificationsByMemberId(
            memberId,
            LocalDateTime.now());
    }

    @Transactional
    public void markAsReadLikeNotification(Long notificationId) {
        EpisodeLikeNotification notification = episodeLikeNotificationRepository.findById(
                notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));
        notification.markAsRead();
        episodeLikeNotificationRepository.save(notification);
    }

    @Transactional
    public void markAsReadEpisodeReportNotification(Long notificationId) {
        EpisodeReportNotification notification = episodeReportNotificationRepository.findById(
                notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));
        notification.markAsRead();
        episodeReportNotificationRepository.save(notification);
    }

    @Transactional
    public void markAsReadCommentReportNotification(Long notificationId) {
        CommentReportNotification notification = commentReportNotificationRepository.findById(
                notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));
        notification.markAsRead();
        commentReportNotificationRepository.save(notification);
    }

    @Transactional
    public void markAsReadCommentRecOrNotRecNotification(Long notificationId) {
        CommentRecOrNotRecNotification notification = commentRecOrNotRecNotificationRepository.findById(
                notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));
        notification.markAsRead();
        commentRecOrNotRecNotificationRepository.save(notification);
    }

    @Transactional
    public void markAsReadRecommentReportNotification(Long notificationId) {
        RecommentReportNotification notification = recommentReportNotificationRepository.findById(
                notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));
        notification.markAsRead();
        recommentReportNotificationRepository.save(notification);
    }

    @Transactional
    public void markAsReadRecommentRecOrNotRecNotification(Long notificationId) {
        RecommentRecOrNotRecNotification notification = recommentRecOrNotRecNotificationRepository.findById(
                notificationId)
            .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));
        notification.markAsRead();
        recommentRecOrNotRecNotificationRepository.save(notification);
    }

    @Transactional
    public void cleanupExpiredLikeNotifications() {
        List<EpisodeLikeNotification> expiredNotifications = episodeLikeNotificationRepository
            .findExpiredNotifications(LocalDateTime.now());

        episodeLikeNotificationRepository.deleteAll(expiredNotifications);
        log.info("만료된 좋아요 알림 {}개 삭제 완료", expiredNotifications.size());
    }

    @Transactional
    public void cleanupExpiredEpisodeReportNotifications() {
        List<EpisodeReportNotification> expiredNotifications = episodeReportNotificationRepository
            .findExpiredNotifications(LocalDateTime.now());

        episodeReportNotificationRepository.deleteAll(expiredNotifications);
        log.info("만료된 에피소드 신고 알림 {}개 삭제 완료", expiredNotifications.size());
    }

    @Transactional
    public void cleanupExpiredCommentReportNotifications() {
        List<CommentReportNotification> expiredNotifications = commentReportNotificationRepository
            .findExpiredCommentReportNotifications(LocalDateTime.now());

        commentReportNotificationRepository.deleteAll(expiredNotifications);
        log.info("만료된 댓글 신고 알림 {}개 삭제 완료", expiredNotifications.size());
    }

    @Transactional
    public void cleanupExpiredCommentRecOrNotRecNotifications() {
        List<CommentRecOrNotRecNotification> expiredNotifications = commentRecOrNotRecNotificationRepository
            .findExpiredNotifications(LocalDateTime.now());

        commentRecOrNotRecNotificationRepository.deleteAll(expiredNotifications);
        log.info("만료된 댓글 추천/비추천 알림 {}개 삭제 완료", expiredNotifications.size());
    }

    @Transactional
    public void cleanupExpiredRecommentReportNotifications() {
        List<RecommentReportNotification> expiredNotifications = recommentReportNotificationRepository
            .findExpiredRecommentReportNotifications(LocalDateTime.now());

        recommentReportNotificationRepository.deleteAll(expiredNotifications);
        log.info("만료된 대댓글 신고 알림 {}개 삭제 완료", expiredNotifications.size());
    }

    @Transactional
    public void cleanupExpiredRecommentRecOrNotRecNotifications() {
        List<RecommentRecOrNotRecNotification> expiredNotifications = recommentRecOrNotRecNotificationRepository
            .findExpiredNotifications(LocalDateTime.now());

        recommentRecOrNotRecNotificationRepository.deleteAll(expiredNotifications);
        log.info("만료된 대댓글 추천/비추천 알림 {}개 삭제 완료", expiredNotifications.size());
    }
}