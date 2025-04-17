package pretzel.dreamketcherbe.domain.member.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findBySocialId(String socialId);

    boolean existsByNickname(String uniqueNickname);

    Optional<Member> findById(Long memberId);

    boolean existsByBusinessEmailAndIdNot(String newBusinessEmail, Long memberId);

    boolean existsByNicknameAndIdNot(String newNickname, Long memberId);

    @Query("""
            SELECT DISTINCT w.member
            FROM Webtoon w
            WHERE (LOWER(w.member.nickname) LIKE %:keyword%
            OR LOWER(REPLACE(w.member.nickname,' ','')) LIKE %:keyword%)
            AND w.isDeleted = false
        """)
    List<Member> findDistinctMembersByNickname(
        @Param("keyword") String keyword,
        Pageable pageable
    );
}
