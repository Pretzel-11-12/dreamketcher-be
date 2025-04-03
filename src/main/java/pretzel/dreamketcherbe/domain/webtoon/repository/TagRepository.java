package pretzel.dreamketcherbe.domain.webtoon.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.webtoon.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByContent(String content);
}
