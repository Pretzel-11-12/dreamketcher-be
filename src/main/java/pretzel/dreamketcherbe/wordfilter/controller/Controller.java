package pretzel.dreamketcherbe.wordfilter.controller;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.wordfilter.dto.CreateWordReqDto;
import pretzel.dreamketcherbe.wordfilter.entity.AllowedWord;
import pretzel.dreamketcherbe.wordfilter.entity.BadWord;
import pretzel.dreamketcherbe.wordfilter.service.WordService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/word-filter")
public class Controller {

    private final WordService wordService;

    @PostMapping("/add/bad-word")
    public ResponseEntity<Void> addBadWord(@Auth Long memberId,
        @RequestBody CreateWordReqDto request) {
        wordService.addBadWord(request.word());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/delete/bad-word/{wordId}")
    public ResponseEntity<Void> deleteBadWord(@Auth Long memberId,
        @PathVariable Long wordId) {
        wordService.deleteBadWord(wordId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/bad-words")
    public ResponseEntity<List<BadWord>> getAllBadWords(@Auth Long memberId) {
        List<BadWord> badWords = wordService.getAllBadWords();

        return ResponseEntity.ok(badWords);
    }

    @PostMapping("/add/allowed-word")
    public ResponseEntity<Void> addAllowedWord(@Auth Long memberId,
        @RequestBody CreateWordReqDto request) {
        wordService.addAllowedWord(request.word());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/delete/allowed-word/{wordId}")
    public ResponseEntity<Void> deleteAllowedWord(@Auth Long memberId,
        @PathVariable Long wordId) {
        wordService.deleteAllowedWord(wordId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/allowed-words")
    public ResponseEntity<List<AllowedWord>> getAllAllowedWords(@Auth Long memberId) {
        List<AllowedWord> allowedWords = wordService.getAllAllowedWords();

        return ResponseEntity.ok(allowedWords);
    }
}
