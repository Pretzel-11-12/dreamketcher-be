package pretzel.dreamketcherbe.domain.webtoon.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.UpdateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.WebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.service.WebtoonService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/webtoons")
@AllArgsConstructor
public class WebtoonController {

    private final WebtoonService webtoonService;

    /**
     * 장르별 웹툰 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<WebtoonResDto>> getWebtoonsByGenre(@RequestParam String genre) {
        return ResponseEntity.ok(webtoonService.getWebtoonsByGenre(genre));
    }

    /**
     * 웹툰 완결 목록 조회
     */
    @GetMapping("/finish")
    public ResponseEntity<List<WebtoonResDto>> getWebtoonsByFinish() {
        return ResponseEntity.ok(webtoonService.getWebtoonsByFinish());
    }

    /**
     * 웹툰 신작 목록 조회
     */
    @GetMapping("/new")
    public ResponseEntity<List<WebtoonResDto>> getWebtoonsByNew() {
        return ResponseEntity.ok(webtoonService.getWebtoonsByNew());
    }

    /**
     * 웹툰 등록
     */
    @PostMapping
    public ResponseEntity<CreateWebtoonResDto> createWebtoon(@Auth Long memberId,
        @RequestBody @Valid
        CreateWebtoonReqDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(webtoonService.createWebtoon(memberId, request));
    }

    /**
     * 관심 웹툰 추가
     */
    @PostMapping("/{webtoonId}/favorite")
    public ResponseEntity<Void> addFavoriteWebtoon(@Auth Long memberId,
                                                   @PathVariable Long webtoonId) {
        webtoonService.addFavoriteWebtoon(memberId, webtoonId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * 웹툰 수정
     */
    @PutMapping("/{webtoonId}")
    public ResponseEntity<Void> updateWebtoon(@Auth Long memberId,
        @PathVariable("webtoonId") Long webtoonId,
        @RequestBody @Valid UpdateWebtoonReqDto request) {
        webtoonService.updateWebtoon(memberId, webtoonId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 웹툰 삭제
     */
    @DeleteMapping("/{webtoonId}")
    public ResponseEntity<Void> deleteWebtoon(@Auth Long memberId,
        @PathVariable("webtoonId") Long webtoonId) {
        webtoonService.deleteWebtoon(memberId, webtoonId);
        return ResponseEntity.ok().build();
    }
}
