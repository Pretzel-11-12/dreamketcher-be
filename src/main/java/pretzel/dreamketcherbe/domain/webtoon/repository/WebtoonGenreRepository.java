package pretzel.dreamketcherbe.domain.webtoon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonGenre;

import java.util.List;

public interface WebtoonGenreRepository extends JpaRepository<WebtoonGenre, Long> {

    List<WebtoonGenre> findByGenreId(Long genreId);
}
