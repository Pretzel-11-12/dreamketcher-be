package pretzel.dreamketcherbe.domain.comment.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;

public interface RecommentRepository extends JpaRepository<Recomment, Long> {

    @Query("SELECT r FROM Recomment r WHERE r.comment.id = :commentId AND r.isDeleted = false ORDER BY r.commentOrder ASC")
    Page<Recomment> findActiveRecommentsByParentCommentId(Long commentId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Recomment r WHERE r.comment.id = :commentId AND r.isDeleted = false ORDER BY r.commentOrder ASC")
    long countByParentCommentIdAndIsDeletedFalse(Long commentId);

    @Query("SELECT r.id FROM Recomment r")
    List<Long> findAllRecommentIds();

    @Modifying
    @Query("UPDATE Recomment r SET r.isDeleted = true WHERE r.comment.id IN :commentIds")
    void deleteByCommentId(@Param("commentIds") List<Long> commentIds);

}
