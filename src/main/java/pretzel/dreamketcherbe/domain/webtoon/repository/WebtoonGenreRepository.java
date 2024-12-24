package pretzel.dreamketcherbe.domain.webtoon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonGenre;

import java.util.List;

public interface WebtoonGenreRepository extends JpaRepository<WebtoonGenre, Long> {
    List<WebtoonGenre> findByWebtoonId(Long webtoonId);

    @Query("SELECT wg FROM WebtoonGenre wg WHERE wg.genre.id = :genreId AND wg.webtoon.status = 'IN_SERIES'")
    Page<WebtoonGenre> findByGenreIdAndStatus(Long genreId, Pageable pageable);
}
