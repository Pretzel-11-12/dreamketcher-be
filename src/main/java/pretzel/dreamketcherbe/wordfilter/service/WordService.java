package pretzel.dreamketcherbe.wordfilter.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.wordfilter.entity.AllowedWord;
import pretzel.dreamketcherbe.wordfilter.entity.BadWord;
import pretzel.dreamketcherbe.wordfilter.event.WordReloadEvent;
import pretzel.dreamketcherbe.wordfilter.filtering.WordFilter;
import pretzel.dreamketcherbe.wordfilter.repository.AllowedWordRepository;
import pretzel.dreamketcherbe.wordfilter.repository.BadWordRepository;

@Service
@RequiredArgsConstructor
public class WordService {

    private final ApplicationEventPublisher eventPublisher;
    private final BadWordRepository badWordRepository;
    private final AllowedWordRepository allowedWordRepository;
    private final WordFilter wordFilter;

    public void addBadWord(String word) {
        if (badWordRepository.existsByWord(word)) {
            throw new IllegalArgumentException("이미 존재하는 금지어입니다.");
        }

        badWordRepository.save(new BadWord(word));
        eventPublisher.publishEvent(new WordReloadEvent());
    }

    public void deleteBadWord(Long wordId) {
        badWordRepository.deleteById(wordId);
        eventPublisher.publishEvent(new WordReloadEvent());
    }

    public List<BadWord> getAllBadWords() {
        return badWordRepository.findAll();
    }

    public void addAllowedWord(String word) {
        if (allowedWordRepository.existsByWord(word)) {
            throw new IllegalArgumentException("이미 존재하는 허용어입니다.");
        }

        allowedWordRepository.save(new AllowedWord(word));
        eventPublisher.publishEvent(new WordReloadEvent());
    }

    public void deleteAllowedWord(Long wordId) {
        allowedWordRepository.deleteById(wordId);
        eventPublisher.publishEvent(new WordReloadEvent());
    }

    public List<AllowedWord> getAllAllowedWords() {
        return allowedWordRepository.findAll();
    }
}
