package pretzel.dreamketcherbe.domain.webtoon.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

}
