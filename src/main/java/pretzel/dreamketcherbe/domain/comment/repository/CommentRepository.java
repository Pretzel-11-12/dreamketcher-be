package pretzel.dreamketcherbe.domain.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.episode.id = :episodeId AND c.status = 'NORMAL' AND c.episode.published = true ")
    Page<Comment> findByEpisodeId(Long episodeId, Pageable pageables);

    @Query("SELECT c.id FROM Comment c")
    List<Long> findAllCommentIds();

    @Query("SELECT c FROM Comment c WHERE c.member.id = :memberId AND c.status = 'NORMAL'")
    List<Comment> findByMemberIdAndDeletedFalse(@Param("memberId") Long memberId, Sort sort);

    @Query("SELECT c.id FROM Comment c WHERE c.episode.id IN :episodeIds AND c.status = 'NORMAL' AND c.episode.published = true")
    List<Long> findByEpisodeIds(@Param("episodeIds") List<Long> episodeIds);

    @Query("SELECT c.id FROM Comment c WHERE c.episode.id = :episodeId AND c.status = 'NORMAL' AND c.episode.published = true")
    List<Long> findByEpisodeId(@Param("episodeId") Long episodeId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.status = 'DELETED' WHERE c.episode.id IN :episodeIds AND c.status = 'NORMAL' AND c.episode.published = true")
    void deleteByEpisodeId(@Param("episodeIds") List<Long> episodeIds);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.status = 'DELETED' WHERE c.episode.id = :episodeId AND c.status = 'NORMAL' AND c.episode.published = true")
    void deleteByEpisode(@Param("episodeId") Long episodeId);

}
