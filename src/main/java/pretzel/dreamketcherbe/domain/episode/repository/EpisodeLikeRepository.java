package pretzel.dreamketcherbe.domain.episode.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.episode.entity.EpisodeLike;

public interface EpisodeLikeRepository extends JpaRepository<EpisodeLike, Long> {

    long countByEpisodeId(Long episodeId);

    @Query("SELECT COUNT(el) FROM EpisodeLike el WHERE el.episode.id = :episodeId AND el.member.id = :memberId")
    Optional<EpisodeLike> deleteByEpisodeAndMember(@Param("episodeId") Long episodeId,
        @Param("memberId") Long memberId);

    List<EpisodeLike> findAllById(Long episodeId);
}