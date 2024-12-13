package pretzel.dreamketcherbe.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;
import pretzel.dreamketcherbe.domain.member.entity.Member;

import java.util.List;

public interface InterestedWebtoonRepository extends JpaRepository<InterestedWebtoon, Long> {
    List<InterestedWebtoon> findAllByMemberId(Member member);
}
