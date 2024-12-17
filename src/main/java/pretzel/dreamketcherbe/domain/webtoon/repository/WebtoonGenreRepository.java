package pretzel.dreamketcherbe.domain.webtoon.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonGenre;

public interface WebtoonGenreRepository extends JpaRepository<WebtoonGenre, Long> {

    List<WebtoonGenre> findByGenreId(Long genreId);

    Optional<WebtoonGenre> findByWebtoonId(Long webtoonId);
}
