package pretzel.dreamketcherbe.domain.episode.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class EpisodeScheduler {

    private final EpisodeService episodeService;

    private final RedisTemplate redisTemplate;

    public static final String EPISODE_LIKE_COUNT_KEY_PREFIX = "episode:likeCount:";

    /**
     * 좋아요 수 동기화
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void syncEpisodeLikeCount() {
        Set<String> keys = redisTemplate.keys(EPISODE_LIKE_COUNT_KEY_PREFIX + "*");

        if (keys != null) {
            for (String key : keys) {
                Long episodeId = extractEpisodeId(key);

                episodeService.syncEpisodeLikeCount(episodeId);
            }
        }
    }

    /**
     * 에피소드 좋아요수 초기화 및 재동기화
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void initializeRedisLikeCount() {
        Set<String> keys = redisTemplate.keys(EPISODE_LIKE_COUNT_KEY_PREFIX + "*");

        if (keys != null) {
            for (String key : keys) {
                Long episodeId = extractEpisodeId(key);

                episodeService.initializeRedisLikeCount(episodeId);
            }
        }
    }

    private Long extractEpisodeId(String key) {
        String idString = key.replace(EPISODE_LIKE_COUNT_KEY_PREFIX, "");
        return Long.parseLong(idString);
    }
}
