package pretzel.dreamketcherbe.domain.comment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.comment.entity.NotRecommendation;

public interface NotRecommendationRepository extends JpaRepository<NotRecommendation, Long> {

    @Query("SELECT r FROM NotRecommendation r WHERE r.member.id = :memberId AND r.comment.id = :commentId")
    Optional<NotRecommendation> findByMemberAndComment(@Param("memberId") Long memberId,
        @Param("commentId") Long commentId);

    @Modifying
    @Query("DELETE FROM NotRecommendation r WHERE r.member.id = :memberId AND r.comment.id = :commentId")
    void deleteByMemberAndComment(@Param("memberId") Long memberId,
        @Param("commentId") Long commentId);

    @Modifying
    @Query("DELETE FROM NotRecommendation r WHERE r.comment.id IN :commentIds")
    void deleteByComment(@Param("commentIds") List<Long> commentIds);

    @Modifying
    @Query("DELETE FROM NotRecommendation r WHERE r.comment.id = :commentId")
    void deleteBycommentId(@Param("commentId") Long commentId);
}
