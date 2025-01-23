package pretzel.dreamketcherbe.domain.episode.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.episode.entity.EpisodeStar;

public interface EpisodeStarRepository extends JpaRepository<EpisodeStar, Long> {

    List<EpisodeStar> findByEpisodeId(Long episodeId);

    Optional<EpisodeStar> findByMemberIdAndEpisodeId(Long memberId, Long episodeId);

    @Query("SELECT es.webtoon.id, COUNT(DISTINCT es.member.id) FROM EpisodeStar es WHERE es.webtoon.id IN :webtoonIds GROUP BY es.webtoon.id")
    List<Object[]> countDistinctStarsByWebtoonIds(List<Long> webtoonIds);

    @Modifying
    @Query("DELETE FROM EpisodeStar es WHERE es.episode.id IN :episodeIds")
    void deleteByEpisode(@Param("episodeIds") List<Long> episodeIds);


    @Modifying
    @Query("DELETE FROM EpisodeStar es WHERE es.episode.id = :episodeId")
    void deleteByEpisodeId(@Param("episodeId") Long episodeId);
}
