package pretzel.dreamketcherbe.domain.episode.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    // 동일 날짜 미발행 웹툰 조회
    List<Episode> findByPublishedAtAndPublishedFalse(LocalDate publishedAt);

}