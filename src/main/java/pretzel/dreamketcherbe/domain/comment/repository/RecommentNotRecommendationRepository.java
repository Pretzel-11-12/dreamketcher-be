package pretzel.dreamketcherbe.domain.comment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.comment.entity.RecommentNotRecommendation;

public interface RecommentNotRecommendationRepository extends
    JpaRepository<RecommentNotRecommendation, Long> {

    @Query("SELECT r FROM RecommentNotRecommendation r WHERE r.member.id = :memberId AND r.recomment.id = :recommentId")
    Optional<RecommentNotRecommendation> findByMemberAndRecomment(
        @Param("memberId") Long memberId, @Param("recoomentId") Long recommentId);
}
