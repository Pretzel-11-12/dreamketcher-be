package pretzel.dreamketcherbe.domain.webtoon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.MyWebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.SearchedWebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.UpdateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.WebtoonResDto;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;
import pretzel.dreamketcherbe.domain.webtoon.service.WebtoonService;

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

        CreateWebtoonResDto webtoon = webtoonService.createWebtoon(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(webtoon);
    }

    /**
     * 웹툰 썸네일 등록
     */
    @PostMapping("/upload/thumbnail")
    public ResponseEntity<String> uploadWebtoonThumbnail(@Auth Long memberId,
        @RequestParam("image") MultipartFile thumbnail) {
        String thumbnailUrl = webtoonService.uploadThumbnail(memberId, thumbnail);

        return ResponseEntity.ok(thumbnailUrl);
    }

    /**
     * 웹툰 썸네일 수정
     */
    @PutMapping("/{webtoonId}/thumbnail")
    public ResponseEntity<String> updateWebtoonThumbnail(@Auth Long memberId,
        @PathVariable Long webtoonId,
        @RequestParam("oldThumbnail") String oldThumbnail,
        @RequestParam("newThumbnail") MultipartFile newThumbnail,
        @RequestParam("folderName") String folderName) {
        String updatedThumbnailUrl = webtoonService.updateThumbnail(oldThumbnail, newThumbnail,
            folderName);

        return ResponseEntity.ok(updatedThumbnailUrl);
    }

    /**
     * 웹툰 프롤로그 등록
     */
    @PostMapping("/upload/prologue")
    public ResponseEntity<String> uploadWebtoonPrologue(@Auth Long memberId,
        @RequestParam("images") List<MultipartFile> images, ObjectMapper objectMapper) {
        String prologueUrls = webtoonService.uploadPrologue(memberId, images, objectMapper);

        return ResponseEntity.ok(prologueUrls);
    }

    /**
     * 웹툰 프롤로그 수정
     */
    @PutMapping("/{webtoonId}/prologue")
    public ResponseEntity<String> updateWebtoonPrologue(@Auth Long memberId,
        @PathVariable Long webtoonId,
        @RequestParam("existingUrls") String existingUrls,
        @RequestParam("newImages") List<MultipartFile> newImages,
        @RequestParam("replaceIndices") List<Integer> replaceIndices,
        @RequestParam("folderName") String folderName,
        ObjectMapper objectMapper) {
        String updatedPrologueUrls = webtoonService.updatePrologue(existingUrls,
            newImages,
            replaceIndices, folderName, objectMapper);

        return ResponseEntity.ok(updatedPrologueUrls);
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

    // TODO: Redis 적용 후 관심 웹툰 삭제 메서드 추가

    /**
     * 내 작품 조회
     */
    @GetMapping("/{webtoonId}")
    public ResponseEntity<MyWebtoonResDto> getWebtoon(
        @Auth Long memberId,
        @PathVariable Long webtoonId) {
        return ResponseEntity.ok(webtoonService.getMyWebtoon(webtoonId));
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
    public ResponseEntity<List<SearchedWebtoonResDto>> searchWebtoon(
        @RequestParam String keyword) {
        return ResponseEntity.ok(webtoonService.searchWebtoon(keyword));
    }
}
