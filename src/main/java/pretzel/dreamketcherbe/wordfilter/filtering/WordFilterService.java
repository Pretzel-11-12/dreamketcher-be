package pretzel.dreamketcherbe.wordfilter.filtering;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.wordfilter.entity.AllowedWord;
import pretzel.dreamketcherbe.wordfilter.entity.BadWord;
import pretzel.dreamketcherbe.wordfilter.event.WordReloadEvent;
import pretzel.dreamketcherbe.wordfilter.repository.AllowedWordRepository;
import pretzel.dreamketcherbe.wordfilter.repository.BadWordRepository;

@Service
@RequiredArgsConstructor
public class WordFilterService {

    private final WordFilter wordFilter;
    private final BadWordRepository badWordRepository;
    private final AllowedWordRepository allowedWordRepository;

    @PostConstruct
    public void init() {
        reload();
    }

    public String filter(String input) {
        return wordFilter.filter(input);
    }
    
    public void reload() {
        List<String> badWords = badWordRepository.findAll().stream()
            .map(BadWord::getWord)
            .toList();

        List<String> allowedWords = allowedWordRepository.findAll().stream()
            .map(AllowedWord::getWord)
            .toList();

        wordFilter.reload(badWords, allowedWords);
    }

    @EventListener
    public void handleWordReloadEvent(WordReloadEvent event) {
        reload();
    }
}
