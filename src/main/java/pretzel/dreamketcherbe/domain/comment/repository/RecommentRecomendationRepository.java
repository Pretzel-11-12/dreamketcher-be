package pretzel.dreamketcherbe.domain.comment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.comment.entity.RecommentRecommendation;

public interface RecommentRecomendationRepository extends
    JpaRepository<RecommentRecommendation, Long> {

    @Query("SELECT r FROM RecommentRecommendation r WHERE r.member.id = :memberId AND r.recomment.id = :recommentId")
    Optional<RecommentRecommendation> findByMemberAndRecomment(@Param("memberId") Long memberId,
        @Param("recommentId") Long recommentId);
}
