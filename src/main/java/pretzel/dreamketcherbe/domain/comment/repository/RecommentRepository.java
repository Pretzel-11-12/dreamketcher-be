package pretzel.dreamketcherbe.domain.comment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;

public interface RecommentRepository extends JpaRepository<Recomment, Long> {

    @Query("SELECT r FROM Recomment r WHERE r.parentCommentId = :parentCommentId AND r.isDeleted = false")
    List<Recomment> findActiveRecommentsByParentCommentId(Long parentCommentId);

    long countByParentCommentIdAndIsDeletedFalse(Long parentCommentId);

}
