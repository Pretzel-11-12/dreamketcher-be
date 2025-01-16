package pretzel.dreamketcherbe.domain.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.episode.id = :episodeId AND c.isDeleted = false ")
    Page<Comment> findByEpisodeId(Long episodeId, Pageable pageables);

    @Query("SELECT c.id FROM Comment c")
    List<Long> findAllCommentIds();

}
