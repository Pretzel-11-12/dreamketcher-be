package pretzel.dreamketcherbe.domain.webtoon.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonResDto;
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
}
