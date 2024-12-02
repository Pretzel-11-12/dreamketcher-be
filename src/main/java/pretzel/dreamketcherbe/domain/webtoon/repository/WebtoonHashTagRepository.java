package pretzel.dreamketcherbe.domain.webtoon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonHashTag;

import java.util.List;

public interface WebtoonHashTagRepository extends JpaRepository<WebtoonHashTag, Long> {

    List<WebtoonHashTag> findByHashTagId(Long hashTageId);
}
