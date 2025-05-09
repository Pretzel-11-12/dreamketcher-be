package pretzel.dreamketcherbe.domain.comment.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;

public interface RecommentRepository extends JpaRepository<Recomment, Long> {

    @Query("SELECT r FROM Recomment r WHERE r.comment.id = :commentId AND r.status = 'NORMAL' ORDER BY r.commentOrder ASC")
    Page<Recomment> findActiveRecommentsByParentCommentId(Long commentId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Recomment r WHERE r.comment.id = :commentId AND r.status = 'NORMAL' ORDER BY r.commentOrder ASC")
    long countByParentCommentIdAndIsDeletedFalse(Long commentId);

    @Query("SELECT r.id FROM Recomment r")
    List<Long> findAllRecommentIds();

    @Query("SELECT r FROM Recomment r WHERE r.member.id = :memberId AND r.status = 'NORMAL'")
    List<Recomment> findByMemberIdAndIsDeletedFalse(@Param("memberId") Long memberId, Sort sort);

    @Query("SELECT r.id FROM Recomment r WHERE r.comment.id = :commentId AND r.status = 'NORMAL'")
    List<Long> findByComment(@Param("commentId") Long commentId);

    @Query("SELECT r.id FROM Recomment r WHERE r.comment.id IN :commentIds AND r.status = 'NORMAL'")
    List<Long> findBycommentId(@Param("commentIds") List<Long> commentIds);

    @Modifying
    @Query("UPDATE Recomment r SET r.status = 'DELETED' WHERE r.comment.id IN :commentIds")
    void deleteByCommentId(@Param("commentIds") List<Long> commentIds);

    @Modifying
    @Query("UPDATE Recomment r SET r.status = 'DELETED' WHERE r.comment.id = :commentId")
    void deleteByComment(@Param("commentId") Long commentId);

    @Modifying
    @Transactional
    @Query(
        "UPDATE Recomment r " + "SET r.recommendationCount = :recommendCount, "
            + "r.notRecommendationCount = :notRecommendCount " + "WHERE r.id = :recommentId"
    )
    int updateCount(@Param("recommentId") Long recommentId,
        @Param("recommendCount") int recommendCount,
        @Param("notRecommendCount") int notRecommendCount);
}
