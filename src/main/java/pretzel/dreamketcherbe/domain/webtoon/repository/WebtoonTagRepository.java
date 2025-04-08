package pretzel.dreamketcherbe.domain.webtoon.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonTag;

public interface WebtoonTagRepository extends JpaRepository<WebtoonTag, Long> {

    List<WebtoonTag> findAllByTagContent(String content);

    List<WebtoonTag> findAllByWebtoonId(Long webtoonId);

    @Query("select wt.webtoon from WebtoonTag wt where wt.tag.id = :tagId")
    List<Webtoon> findWebtoonsByTagId(@Param("tagId") Long tagId);
}
