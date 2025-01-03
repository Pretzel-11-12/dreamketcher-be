package pretzel.dreamketcherbe.domain.ranking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.domain.ranking.dto.RankingResDto;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonStatus;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.GenreRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final WebtoonRepository webtoonRepository;

    private final GenreRepository genreRepository;

    /**
     * 전체 랭킹 목록 조회
     */
    public List<RankingResDto> getAllRanking(String genre) {
        if (!isValidGenre(genre)) {
            throw new WebtoonException(WebtoonExceptionType.GENRE_NOT_FOUND);
        }

        return webtoonRepository.findRanking(WebtoonStatus.IN_SERIES.getStatus(), genre).stream()
            .map(RankingResDto::of)
            .toList();
    }

    /**
     * 신작 랭킹 목록 조회
     */
    public List<RankingResDto> getNewRanking(String genre) {
        if (!isValidGenre(genre)) {
            throw new WebtoonException(WebtoonExceptionType.GENRE_NOT_FOUND);
        }

        return webtoonRepository.findRanking(WebtoonStatus.NEW.getStatus(), genre).stream()
            .map(RankingResDto::of)
            .toList();
    }

    /**
     * 완결 랭킹 목록 조회
     */
    public List<RankingResDto> getFinishRanking(String genre) {
        if (!isValidGenre(genre)) {
            throw new WebtoonException(WebtoonExceptionType.GENRE_NOT_FOUND);
        }

        return webtoonRepository.findRanking(WebtoonStatus.FINISH.getStatus(), genre).stream()
            .map(RankingResDto::of)
            .toList();
    }

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
