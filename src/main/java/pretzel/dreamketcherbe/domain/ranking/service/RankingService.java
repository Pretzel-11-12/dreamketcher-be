package pretzel.dreamketcherbe.domain.ranking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.ranking.dto.RankingResDto;
import pretzel.dreamketcherbe.domain.ranking.dto.WebtoonPopularityDataDto;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final WebtoonRepository webtoonRepository;

    /**
     * 전체 랭킹 목록 조회
     */
    public List<RankingResDto> getAllRanking() {
        return getAllPopularityDataDto().stream()
            .map(RankingResDto::of)
            .toList();
    }

    /**
     * 전체 + 장르 랭킹 목록 조회
     */
    public List<RankingResDto> getAllRankingByGenre(String genre) {
        return getAllPopularityDataDtoByGenre(genre).stream()
            .map(RankingResDto::of)
            .toList();
    }

    /**
     * 신작 랭킹 목록 조회
     */
    public List<RankingResDto> getNewRanking(String genre) {
        if (genre.equals("none")) {
            return getNewPopularityDataDto().stream()
                .map(RankingResDto::of)
                .toList();
        }

        return getNewPopularityDataDtoByGenre(genre).stream()
            .map(RankingResDto::of)
            .toList();
    }

    /**
     * 전체 랭킹 인기 dto 가져오는 메서드
     */
    private List<WebtoonPopularityDataDto> getAllPopularityDataDto() {
        return webtoonRepository.findAllWithPopularityData().stream()
                .map(WebtoonPopularityDataDto::of)
                .sorted(Comparator.comparing(WebtoonPopularityDataDto::getPopularity).reversed())
                .limit(12)
                .toList();
    }

    /**
     * 전체 + 장르 인기 dto 가져오는 메서드
     */
    private List<WebtoonPopularityDataDto> getAllPopularityDataDtoByGenre(String genre) {
        return webtoonRepository.findAllWithPopularityData().stream()
                .map(WebtoonPopularityDataDto::of)
                .filter(webtoon -> webtoon.getGenres().contains(genre))
                .sorted(Comparator.comparing(WebtoonPopularityDataDto::getPopularity).reversed())
                .limit(12)
                .toList();
    }

    /**
     * 신작 랭킹 dto 가져오는 메서드
     */
    private List<WebtoonPopularityDataDto> getNewPopularityDataDto() {
        return webtoonRepository.findNewWithPopularityData().stream()
            .map(WebtoonPopularityDataDto::of)
            .sorted(Comparator.comparing(WebtoonPopularityDataDto::getPopularity).reversed())
            .limit(12)
            .toList();
    }

    /**
     * 신작 + 장르 dto 가져오는 메서드
     */
    private List<WebtoonPopularityDataDto> getNewPopularityDataDtoByGenre(String genre) {
        return webtoonRepository.findNewWithPopularityData().stream()
            .map(WebtoonPopularityDataDto::of)
            .filter(webtoon -> webtoon.getGenres().contains(genre))
            .sorted(Comparator.comparing(WebtoonPopularityDataDto::getPopularity).reversed())
            .limit(12)
            .toList();
    }
}
