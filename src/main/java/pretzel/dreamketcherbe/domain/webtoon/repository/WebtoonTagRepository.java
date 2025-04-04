package pretzel.dreamketcherbe.domain.webtoon.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonTag;

public interface WebtoonTagRepository extends JpaRepository<WebtoonTag, Long> {

    List<WebtoonTag> findAllByTagContent(String content);

    List<WebtoonTag> findAllByWebtoonId(Long webtoonId);
}
