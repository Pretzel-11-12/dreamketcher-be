package pretzel.dreamketcherbe.domain.webtoon.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
     * 웹툰 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<WebtoonResDto>> getWebtoons() {
        return ResponseEntity.ok(webtoonService.getWebtoons());
    }

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
     * 웹툰 등록
     */
    @PostMapping
    public ResponseEntity<CreateWebtoonResDto> createWebtoon(@Auth Long memberId,
        @RequestBody @Valid
        CreateWebtoonReqDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(webtoonService.createWebtoon(memberId, request));
    }
}
