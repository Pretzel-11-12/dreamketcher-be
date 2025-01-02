package pretzel.dreamketcherbe.domain.webtoon.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.*;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;
import pretzel.dreamketcherbe.domain.webtoon.service.WebtoonService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/webtoons")
@AllArgsConstructor
public class WebtoonController {

    private final WebtoonService webtoonService;
    private final WebtoonRepository webtoonRepository;
    private final MemberRepository memberRepository;

    /**
     * 연재중인 웹툰 목록 조회
     */
    @GetMapping
    public ResponseEntity<PageResDto<WebtoonResDto>> getWebtoons(
        @RequestParam(defaultValue = "none") String genre,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size,
        @RequestParam(defaultValue = "latest") String order) {
        PageReqDto pageReqDto = PageReqDto.of(genre, page, size, order);
        return ResponseEntity.ok(webtoonService.getWebtoons(pageReqDto));
    }

    /**
     * 웹툰 완결 목록 조회
     */
    @GetMapping("/finish")
    public ResponseEntity<PageResDto<WebtoonResDto>> getWebtoonsByFinish(
        @RequestParam(defaultValue = "none") String genre,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size,
        @RequestParam(defaultValue = "latest") String order) {
        PageReqDto pageReqDto = PageReqDto.of(genre, page, size, order);
        return ResponseEntity.ok(webtoonService.getWebtoonsByFinish(pageReqDto));
    }

    /**
     * 웹툰 신작 목록 조회
     */
    @GetMapping("/new")
    public ResponseEntity<PageResDto<WebtoonResDto>> getWebtoonsByNew(
        @RequestParam(defaultValue = "none") String genre,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size,
        @RequestParam(defaultValue = "latest") String order) {
        PageReqDto pageReqDto = PageReqDto.of(genre, page, size, order);
        return ResponseEntity.ok(webtoonService.getWebtoonsByNew(pageReqDto));
    }

    /**
     * 웹툰 등록
     */
    @PostMapping("/upload")
    public ResponseEntity<CreateWebtoonResDto> createWebtoon(@Auth Long memberId,
        @RequestBody @Valid CreateWebtoonReqDto request) {

        CreateWebtoonResDto response = webtoonService.createWebtoon(memberId, request);
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

    /**
     * 웹툰, 작가 검색
     */
    @GetMapping("/search")
    public ResponseEntity<List<SearchedWebtoonResDto>> searchWebtoon(@RequestParam String keyword) {
        return ResponseEntity.ok(webtoonService.searchWebtoon(keyword));
    }
}
