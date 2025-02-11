package pretzel.dreamketcherbe.domain.ranking.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.ranking.dto.RankingResDto;
import pretzel.dreamketcherbe.domain.ranking.dto.WebtoonPopularityDataDto;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.GenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    private final WebtoonRepository webtoonRepository;

    private final GenreRepository genreRepository;

    private final RedisTemplate<String, WebtoonPopularityDataDto> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    private final int CACHE_REFRESH_INTERVAL = 4 * 60 * 60 * 1000; // 4시간

    private final String RANKING_KEY = "webtoonRanking:";

//    @Scheduled(fixedRate = CACHE_REFRESH_INTERVAL)
//    public void updateRankingCache() {
//        List<Genre> genres = genreRepository.findAll();
//
//        for (Genre genre : genres) {
//            updateRankingForGenre(genre.getName(), WebtoonStatus.IN_SERIES.getStatus());
//            updateRankingForGenre(genre.getName(), WebtoonStatus.NEW.getStatus());
//            updateRankingForGenre(genre.getName(), WebtoonStatus.FINISH.getStatus());
//        }
//
//        log.info("Ranking cache updated at " + LocalDateTime.now());
//    }
//
//
//    /**
//     * 랭킹 목록 조회
//     */
//    public List<RankingResDto> getRanking(String genre, String status) {
//        if (!isValidGenre(genre)) {
//            throw new WebtoonException(WebtoonExceptionType.GENRE_NOT_FOUND);
//        }
//
//        Set<WebtoonPopularityDataDto> cachedRanking = redisTemplate.opsForZSet()
//                .reverseRange(RANKING_KEY + genre + ":" + status, 0, -1);
//
//        if (cachedRanking == null || cachedRanking.isEmpty()) {
//            return Collections.emptyList(); // 캐시에 데이터가 없으면 빈 리스트 반환
//        }
//
//        return cachedRanking.stream()
//                .map(RankingResDto::of)
//                .toList();
//    }

    /**
     * 랭킹 목록 조회 (redis 적용 X)
     */
    public List<RankingResDto> getRanking(String genre, String status) {
        if (!isValidGenre(genre)) {
            throw new WebtoonException(WebtoonExceptionType.GENRE_NOT_FOUND);
        }

        return webtoonRepository.findRanking(status, genre).stream()
                .map(RankingResDto::of)
                .toList();
    }

//    private void updateRankingForGenre(String genre, String status) {
//        List<WebtoonPopularityDataDto> dataDtos = webtoonRepository.findRanking(status, genre);
//        saveRankingToCache(genre, status, dataDtos);
//    }
//
//    private void saveRankingToCache(String genre, String status, List<WebtoonPopularityDataDto> dataDtos) {
//        for (WebtoonPopularityDataDto data : dataDtos) {
//            try {
//                String jsonData = objectMapper.writeValueAsString(data); // 객체를 JSON 문자열로 변환
//                redisTemplate.opsForZSet().add(RANKING_KEY + genre + ":" + status, jsonData, data.getPopularity());
//                System.out.println("Saved to Redis: " + jsonData); // 저장된 데이터 확인 로그
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException("JSON 직렬화 실패", e);
//            }
//        }
//    }

    /**
     * 장르 유효성 검사
     */
    private boolean isValidGenre(String genre) {
        if (genre.equals("none")) {
            return true;
        }
        return genreRepository.existsByName(genre);
    }
}
