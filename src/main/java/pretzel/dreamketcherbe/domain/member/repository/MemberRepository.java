package pretzel.dreamketcherbe.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findBySocialId(String socialId);

    boolean existsByNickname(String uniqueNickname);

    Optional<Member> findById(Long memberId);

    boolean existsByBusinessEmailAndIdNot(String newBusinessEmail, Long memberId);

    boolean existsByNicknameAndIdNot(String newNickname, Long memberId);
}
