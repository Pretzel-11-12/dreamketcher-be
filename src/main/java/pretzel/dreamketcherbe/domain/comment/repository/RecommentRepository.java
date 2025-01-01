package pretzel.dreamketcherbe.domain.comment.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;

public interface RecommentRepository extends JpaRepository<Recomment, Long> {

    @Query("SELECT r FROM Recomment r WHERE r.parentCommentId = :parentCommentId AND r.isDeleted = false")
    Page<Recomment> findActiveRecommentsByParentCommentId(Long parentCommentId, Pageable pageable);

    long countByParentCommentIdAndIsDeletedFalse(Long parentCommentId);

}
