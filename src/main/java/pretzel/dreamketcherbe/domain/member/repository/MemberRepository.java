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

    // New method to find member by nickname
    Optional<Member> findByNickname(String nickname);

    boolean existsByBusinessEmailAndIdNot(String newBusinessEmail, Long memberId);

    boolean existsByNicknameAndIdNot(String newNickname, Long memberId);

    /**
     * 닉네임에 주어진 키워드가 포함된(대소문자 구분 없이, 공백 무시 가능) 멤버 중 삭제되지 않은 웹툰과 연관된 멤버를 중복 없이 페이징하여 조회합니다.
     *
     * @param keyword 멤버 닉네임에서 검색할 키워드
     * @param pageable 페이징 정보
     * @return 조건에 부합하는 멤버 목록
     */
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

    /**
 * 지정된 회원 상태에 따라 회원 목록을 페이지 단위로 조회합니다.
 *
 * @param status 조회할 회원 상태
 * @param pageable 페이지 정보 및 정렬 기준
 * @return 주어진 상태에 해당하는 회원의 페이지별 목록
 */
Page<Member> findAllByStatus(MemberStatus status, Pageable pageable);
}
