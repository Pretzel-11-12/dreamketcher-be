package pretzel.dreamketcherbe.domain.episode.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.episode.entity.EpisodeLike;

public interface EpisodeLikeRepository extends JpaRepository<EpisodeLike, Long> {

    long countByEpisodeId(Long episodeId);

    @Modifying
    @Query("DELETE FROM EpisodeLike el WHERE el.episode.id = :episodeId AND el.member.id = :memberId")
    void deleteByEpisodeAndMember(@Param("episodeId") Long episodeId,
        @Param("memberId") Long memberId);

    List<EpisodeLike> findAllById(Long episodeId);

    @Modifying
    @Query("DELETE FROM EpisodeLike el WHERE el.episode.id IN :episodeIds")
    void deleteByEpisode(@Param("episodeIds") List<Long> episodeIds);
}