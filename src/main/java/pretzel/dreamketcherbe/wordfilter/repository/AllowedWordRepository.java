package pretzel.dreamketcherbe.wordfilter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.wordfilter.entity.AllowedWord;

public interface AllowedWordRepository extends JpaRepository<AllowedWord, Long> {

    boolean existsByWord(String word);

}
