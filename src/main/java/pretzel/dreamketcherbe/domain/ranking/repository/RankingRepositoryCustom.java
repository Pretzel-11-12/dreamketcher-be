package pretzel.dreamketcherbe.domain.ranking.repository;

import pretzel.dreamketcherbe.domain.ranking.dto.WebtoonPopularityDataDto;

import java.util.List;

public interface RankingRepositoryCustom {

    List<WebtoonPopularityDataDto> findRanking(String status, String genreType);
}
