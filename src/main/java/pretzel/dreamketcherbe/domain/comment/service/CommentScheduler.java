package pretzel.dreamketcherbe.domain.comment.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class CommentScheduler {

    private final CommentService commentService;

    private static final String COMMENT_RECOMMEND_PREFIX = "comment:recommendCount:";
    private static final String COMMENT_NOT_RECOMMEND_PREFIX = "comment:notRecommendCount:";
    private static final String COMMENT_RECOMMEND_PATTERN = COMMENT_RECOMMEND_PREFIX + "*";
    private static final String COMMENT_NOT_RECOMMEND_PATTERN = COMMENT_NOT_RECOMMEND_PREFIX + "*";

    private static final String RECOMMENT_RECOMMEND_PREFIX = "recomment:recommendCount:";
    private static final String RECOMMENT_NOT_RECOMMEND_PREFIX = "recomment:notRecommendCount:";
    private static final String RECOMMENT_RECOMMEND_PATTERN = RECOMMENT_RECOMMEND_PREFIX + "*";
    private static final String RECOMMENT_NOT_RECOMMEND_PATTERN =
        RECOMMENT_NOT_RECOMMEND_PREFIX + "*";

    @Scheduled(cron = "0 0 */4 * * ?", zone = "Asia/Seoul") // 4시간 마다 실행
    public void syncAllRecommendationCounts() {
        // 댓글

        Set<Long> allCommentIds = commentService.idUnion(
            COMMENT_RECOMMEND_PATTERN, COMMENT_NOT_RECOMMEND_PATTERN);

        allCommentIds.forEach(id -> {
            int recommend = commentService.getRecommendationCount("comment:recommendCount:" + id);
            int notRecommend = commentService.getRecommendationCount(
                "comment:notRecommendCount:" + id);
            commentService.syncRecommendationCountToDatabase(id, recommend, notRecommend);
        });

        // 답글

        Set<Long> allRecommentIds = commentService.idUnion(
            RECOMMENT_RECOMMEND_PATTERN,
            RECOMMENT_NOT_RECOMMEND_PATTERN
        );

        allRecommentIds.forEach(id -> {
            int recommend = commentService.getRecommentRecommendationCount(
                "recomment:recommendCount:" + id);
            int notRecommend = commentService.getRecommentRecommendationCount(
                "recomment:notRecommendCount:" + id);
            commentService.syncRecommentRecommendationCountToDatabase(id, recommend, notRecommend);
        });
    }

    @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Seoul") // 자정 실행
    public void reloadCommentRedisFromDBScheduler() {
        commentService.reloadCommentRedisFromDB();
    }

    @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Seoul") // 자정 실행
    public void reloadRecommentRedisFromDBScheduler() {
        commentService.reloadRecommentRedisFromDB();
    }
}
