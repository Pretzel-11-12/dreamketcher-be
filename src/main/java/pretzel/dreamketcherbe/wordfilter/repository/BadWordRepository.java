package pretzel.dreamketcherbe.wordfilter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.wordfilter.entity.BadWord;

public interface BadWordRepository extends JpaRepository<BadWord, Long> {

    boolean existsByWord(String word);

}
