package pretzel.dreamketcherbe.domain.episode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.episode.entity.EpisodeLike;

public interface EpisodeLikeRepository extends JpaRepository<EpisodeLike, Long> {

    long countByEpisodeId(Long episodeId);

    boolean existsByEpisodeIdAndMemberId(Long episodeId, Long memberId);

    // 좋아요 해제
    void deleteByEpisodeIdAndMemberId(Long episodeId, Long memberId);
}