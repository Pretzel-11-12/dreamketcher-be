package pretzel.dreamketcherbe.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySocialId(String socialId);

    boolean existsByNickname(String uniqueNickname);
}
