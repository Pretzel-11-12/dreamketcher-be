package pretzel.dreamketcherbe.domain.comment.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentScheduler {

    private final CommentService commentService;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "0 0 * * * ?") // 정각 마다 실행
    public void syncRecommendationCountToDBScheduler() {
        commentService.syncRecommendationCountToDatabase();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void syncRecommentRecommendationCountToDBScheduler() {
        commentService.syncRecommentRecommendationCountToDatabase();
    }

    @Scheduled(cron = "0 0 0 * * ?") // 자정 실행
    public void reloadCommentRedisFromDBScheduler() {
        commentService.reloadCommentRedisFromDB();
    }

    @Scheduled(cron = "0 0 0 * * ?") // 자정 실행
    public void reloadRecommentRedisFromDBScheduler() {
        commentService.reloadRecommentRedisFromDB();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void getCommentRecommendationCount() {
        Set<String> keys = redisTemplate.keys("COMMENT_RECOMMEND_COUNT_KEY_PREFIX:*");

        if (keys != null) {
            for (String key : keys) {
                int count = commentService.getRecommendationCount(key);

                Long commentId = extractCommmentIdFromKey(key);

                commentService.syncRecommendationCountToDatabase();
            }
        }
    }

    private Long extractCommmentIdFromKey(String key) {
        String idString = key.replace("COMMENT_RECOMMEND_COUNT_KEY_PREFIX:", "");
        return Long.parseLong(idString);
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void getRecommentRecommendationCount() {
        Set<String> keys = redisTemplate.keys("RECOMMENT_RECOMMEND_COUNT_KEY_PREFIX:*");

        if (keys != null) {
            for (String key : keys) {
                int count = commentService.getRecommentRecommendationCount(key);

                Long commentId = extractRecommmentIdFromKey(key);

                commentService.syncRecommentRecommendationCountToDatabase();
            }
        }
    }

    private Long extractRecommmentIdFromKey(String key) {
        String idString = key.replace("RECOMMENT_RECOMMEND_COUNT_KEY_PREFIX:", "");
        return Long.parseLong(idString);
    }
}
