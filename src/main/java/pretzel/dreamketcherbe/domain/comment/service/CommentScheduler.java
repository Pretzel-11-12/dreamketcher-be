package pretzel.dreamketcherbe.domain.comment.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pretzel.dreamketcherbe.domain.comment.repository.CommentRepository;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class CommentScheduler {

    private final CommentService commentService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String COMMENT_RECOMMEND_PATTERN = "comment:recommendCount:*";
    private static final String COMMENT_NOT_RECOMMEND_PREFIX = "comment:notRecommendCount:";
    private static final String RECOMMENT_RECOMMEND_PATTERN = "recomment:recommendCount:*";
    private static final String RECOMMENT_NOT_RECOMMEND_PREFIX = "recomment:notRecommendCount:";
    private final CommentRepository commentRepository;

    @Scheduled(cron = "0 0 */4 * * ?", zone = "Asia/Seoul") // 4시간 마다 실행
    public void syncAllRecommendationCounts() {
        // 댓글
        for (String key : scanKeys(COMMENT_RECOMMEND_PATTERN)) {
            Long commentId = extractId(key);
            int recCount = commentService.getRecommendationCount(key);
            int notRecCount = commentService.getRecommendationCount(
                COMMENT_NOT_RECOMMEND_PREFIX + commentId);
            commentService.syncRecommendationCountToDatabase(
                commentId, recCount, notRecCount
            );
        }

        // 답글
        for (String key : scanKeys(RECOMMENT_RECOMMEND_PATTERN)) {
            Long recommentId = extractId(key);
            int recCount = commentService.getRecommentRecommendationCount(key);
            int notRecCount = commentService.getRecommentRecommendationCount(
                RECOMMENT_NOT_RECOMMEND_PREFIX + recommentId);
            commentService.syncRecommentRecommendationCountToDatabase(
                recommentId, recCount, notRecCount
            );
        }
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul") // 자정 실행
    public void reloadCommentRedisFromDBScheduler() {
        commentService.reloadCommentRedisFromDB();
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul") // 자정 실행
    public void reloadRecommentRedisFromDBScheduler() {
        commentService.reloadRecommentRedisFromDB();
    }

    private Long extractId(String key) {
        String stringId = key.substring(key.lastIndexOf(':') + 1);
        return Long.valueOf(stringId);
    }

    private List<String> scanKeys(String pattern) {
        List<String> keys = new ArrayList<>();

        ScanOptions options = ScanOptions.scanOptions()
            .match(pattern)
            .count(100)
            .build();

        try (
            RedisConnection connection = Objects.requireNonNull(
                redisTemplate.getConnectionFactory()).getConnection();
            Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            log.error("스캔 중 에러가 발생했습니다, {}", pattern, e);
        }
        return keys;
    }
}
