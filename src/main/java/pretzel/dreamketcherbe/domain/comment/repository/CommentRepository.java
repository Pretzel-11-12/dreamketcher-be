package pretzel.dreamketcherbe.domain.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.episode.id = :episodeId AND c.isDeleted = false ")
    Page<Comment> findByEpisodeId(Long episodeId, Pageable pageables);

    @Query("SELECT c.id FROM Comment c")
    List<Long> findAllCommentIds();

    @Query("SELECT c FROM Comment c WHERE c.member.id = :memberId AND c.isDeleted = false")
    Page<Comment> findByMemberIdAndDeletedFalse(Long memberId, Pageable pageable);

    @Query("SELECT c.id FROM Comment c WHERE c.episode.id IN :episodeIds AND c.isDeleted = false")
    List<Long> findByEpisodeId(@Param("episodeIds") List<Long> episodeIds);

    @Query("SELECT c.id FROM Comment c WHERE c.episode.id = :episodeId AND c.isDeleted = false")
    List<Long> findByEpisodeId(@Param("episodeId") Long episodeId);

    @Modifying
    @Query("UPDATE Comment c SET c.isDeleted = true WHERE c.episode.id IN :episodeIds")
    void deleteByEpisodeId(@Param("episodeIds") List<Long> episodeIds);

    @Modifying
    @Query("UPDATE Comment c SET c.isDeleted = true WHERE c.episode.id = :episodeId")
    void deleteByEpisode(@Param("episodeId") Long episodeId);

}
