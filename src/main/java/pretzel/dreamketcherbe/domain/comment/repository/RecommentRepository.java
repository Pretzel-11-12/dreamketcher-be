package pretzel.dreamketcherbe.domain.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;

public interface RecommentRepository extends JpaRepository<Recomment, Long> {

    @Query("SELECT r FROM Recomment r WHERE r.comment.id = :commentId AND r.isDeleted = false ORDER BY r.commentOrder ASC")
    Page<Recomment> findActiveRecommentsByParentCommentId(Long commentId, Pageable pageable);

    @Query("SELECT r FROM Recomment r WHERE r.comment.id = :commentId AND r.isDeleted = false ORDER BY r.commentOrder ASC")
    long countByParentCommentIdAndIsDeletedFalse(Long commentId);

}
