package pretzel.dreamketcherbe.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySocialId(String socialId);

    Optional<Member> findById(Long memberId);
}
