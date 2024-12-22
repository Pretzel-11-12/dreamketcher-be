package pretzel.dreamketcherbe.domain.comment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.comment.entity.Recommendation;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    @Query("SELECT r FROM Recommendation r WHERE r.member.id = :memberId AND r.comment.id = :commentId")
    Optional<Recommendation> findByMemberAndComment(@Param("memberId") Long memberId,
        @Param("commentId") Long commentId);

}
