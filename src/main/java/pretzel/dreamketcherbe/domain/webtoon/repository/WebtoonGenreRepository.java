package pretzel.dreamketcherbe.domain.webtoon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonGenre;

import java.util.List;
import java.util.Optional;

public interface WebtoonGenreRepository extends JpaRepository<WebtoonGenre, Long> {

    Optional<WebtoonGenre> findByWebtoonId(Long webtoonId);

    List<WebtoonGenre> findByWebtoon(Webtoon webtoon);

    @Query("SELECT wg.webtoon.id, g.name FROM WebtoonGenre wg JOIN wg.genre g WHERE wg.webtoon.id IN :webtoonIds")
    List<Object[]> findGenresByWebtoonIds(List<Long> webtoonIds);

    @Query("SELECT wg FROM WebtoonGenre wg WHERE wg.genre.id = :genreId AND wg.webtoon.status = 'IN_SERIES'")
    Page<WebtoonGenre> findByGenreIdAndStatus(Long genreId, Pageable pageable);

    List<WebtoonGenre> findAllByGenreId(Long genreId);
}
