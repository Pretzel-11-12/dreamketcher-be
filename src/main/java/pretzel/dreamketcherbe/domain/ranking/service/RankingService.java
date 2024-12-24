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

    public List<RankingResDto> getRanking() {
        return getWebtoonPopularityDataDto().stream()
                .map(RankingResDto::of)
                .toList();
    }

    private List<WebtoonPopularityDataDto> getWebtoonPopularityDataDto() {
        return webtoonRepository.findAllWithPopularityData().stream()
                .map(WebtoonPopularityDataDto::of)
                .sorted(Comparator.comparing(WebtoonPopularityDataDto::getPopularity).reversed())
                .limit(12)
                .toList();
    }
}
