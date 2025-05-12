package pretzel.dreamketcherbe.domain.member.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.MemberStatus;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findBySocialId(String socialId);

    boolean existsByNickname(String uniqueNickname);

    Optional<Member> findById(Long memberId);

    boolean existsByBusinessEmailAndIdNot(String newBusinessEmail, Long memberId);

    boolean existsByNicknameAndIdNot(String newNickname, Long memberId);

    @Query("""
                SELECT DISTINCT w.member
                FROM Webtoon w
                WHERE (LOWER(w.member.nickname) LIKE CONCAT('%', :keyword, '%')
                OR LOWER(REPLACE(w.member.nickname, ' ', '')) LIKE CONCAT('%', :keyword, '%'))
                AND w.isDeleted = false
        """)
    List<Member> findDistinctMembersByNickname(
        @Param("keyword") String keyword,
        Pageable pageable
    );

    Page<Member> findAllByStatus(MemberStatus status, Pageable pageable);
}
