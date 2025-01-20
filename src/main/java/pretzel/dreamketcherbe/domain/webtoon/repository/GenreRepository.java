package pretzel.dreamketcherbe.domain.webtoon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretzel.dreamketcherbe.domain.webtoon.entity.Genre;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByName(String name);

    @Query("select g.id from Genre g where g.name = :name")
    Genre findByGenreName(String name);

    boolean existsByName(String genre);
}
