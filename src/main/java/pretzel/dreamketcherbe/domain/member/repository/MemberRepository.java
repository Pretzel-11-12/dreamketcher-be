package pretzel.dreamketcherbe.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySocialId(String socialId);

    boolean existsByNickname(String uniqueNickname);

    Optional<Member> findById(Long memberId);
}
