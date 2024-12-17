package pretzel.dreamketcherbe.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.member.entity.InterestedWebtoon;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

import java.util.List;
import java.util.Optional;

public interface InterestedWebtoonRepository extends JpaRepository<InterestedWebtoon, Long> {

  List<InterestedWebtoon> findAllByMemberId(Member member);

  Optional<InterestedWebtoon> findByMemberAndWebtoon(Member member, Webtoon webtoon);
}
