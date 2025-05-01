package pretzel.dreamketcherbe.wordfilter.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pretzel.dreamketcherbe.wordfilter.entity.AllowedWord;
import pretzel.dreamketcherbe.wordfilter.entity.BadWord;
import pretzel.dreamketcherbe.wordfilter.filtering.WordFilter;
import pretzel.dreamketcherbe.wordfilter.repository.AllowedWordRepository;
import pretzel.dreamketcherbe.wordfilter.repository.BadWordRepository;

@Service
@RequiredArgsConstructor
public class WordService {

    private final BadWordRepository badWordRepository;
    private final AllowedWordRepository allowedWordRepository;
    private final WordFilter wordFilter;

    public void addBadWord(String word) {
        if (badWordRepository.existsByWord(word)) {
            throw new IllegalArgumentException("이미 존재하는 금지어입니다.");
        }

        badWordRepository.save(new BadWord(word));
        reloadWordFileter();
    }

    public void deleteBadWord(Long wordId) {
        badWordRepository.deleteById(wordId);
        reloadWordFileter();
    }

    public List<BadWord> getAllBadWords() {
        return badWordRepository.findAll();
    }

    public void addAllowedWord(String word) {
        if (allowedWordRepository.existsByWord(word)) {
            throw new IllegalArgumentException("이미 존재하는 허용어입니다.");
        }

        allowedWordRepository.save(new AllowedWord(word));
        reloadWordFileter();
    }

    public void deleteAllowedWord(Long wordId) {
        allowedWordRepository.deleteById(wordId);
        reloadWordFileter();
    }

    public List<AllowedWord> getAllAllowedWords() {
        return allowedWordRepository.findAll();
    }

    private void reloadWordFileter() {
        List<String> badWords = badWordRepository.findAll().stream()
            .map(BadWord::getWord)
            .toList();

        List<String> allowedWords = allowedWordRepository.findAll().stream()
            .map(AllowedWord::getWord)
            .toList();

        wordFilter.reload(badWords, allowedWords);
    }

}
