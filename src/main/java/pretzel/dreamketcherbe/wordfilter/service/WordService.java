package pretzel.dreamketcherbe.wordfilter.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.wordfilter.entity.AllowedWord;
import pretzel.dreamketcherbe.wordfilter.entity.BadWord;
import pretzel.dreamketcherbe.wordfilter.event.WordReloadEvent;
import pretzel.dreamketcherbe.wordfilter.exception.WordFilterException;
import pretzel.dreamketcherbe.wordfilter.exception.WordFilteringExceptionType;
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

    @Transactional
    public void addBadWord(String word) {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("금지어는 null 또는 빈 문자열일 수 없습니다.");
        }
        if (badWordRepository.existsByWord(word)) {
            throw new WordFilterException(WordFilteringExceptionType.BADWORD_ALREADY_EXISTS);
        }

        badWordRepository.save(new BadWord(word));
        eventPublisher.publishEvent(new WordReloadEvent());
    }

    @Transactional
    public void deleteBadWord(Long wordId) {
        if (!badWordRepository.existsById(wordId)) {
            throw new WordFilterException(WordFilteringExceptionType.BADWORD_NOT_FOUND);
        }
        badWordRepository.deleteById(wordId);
        eventPublisher.publishEvent(new WordReloadEvent());
    }

    public List<BadWord> getAllBadWords() {
        return badWordRepository.findAll();
    }

    @Transactional
    public void addAllowedWord(String word) {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("허용어는 null 또는 빈 문자열일 수 없습니다.");
        }

        if (allowedWordRepository.existsByWord(word)) {
            throw new WordFilterException(WordFilteringExceptionType.ALLOWEDWORD_ALREADY_EXISTS);
        }

        allowedWordRepository.save(new AllowedWord(word));
        eventPublisher.publishEvent(new WordReloadEvent());
    }

    @Transactional
    public void deleteAllowedWord(Long wordId) {
        if (!allowedWordRepository.existsById(wordId)) {
            throw new WordFilterException(WordFilteringExceptionType.ALLOWEDWORD_NOT_FOUND);
        }
        allowedWordRepository.deleteById(wordId);
        eventPublisher.publishEvent(new WordReloadEvent());
    }

    public List<AllowedWord> getAllAllowedWords() {
        return allowedWordRepository.findAll();
    }
}
