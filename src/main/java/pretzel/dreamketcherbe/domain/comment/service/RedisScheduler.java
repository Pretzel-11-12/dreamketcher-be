package pretzel.dreamketcherbe.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisScheduler {

    private final CommentService commentService;

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
}
