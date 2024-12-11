package pretzel.dreamketcherbe.domain.admin.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.domain.admin.dto.ManageWebtoonResDto;
import pretzel.dreamketcherbe.domain.admin.service.ManageWebtoonService;

@RestController
@RequestMapping("/api/v1/admin/webtoons")
@AllArgsConstructor
public class ManageWebtoonController {

    private final ManageWebtoonService manageWebtoonService;

    /**
     * 웹툰 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<ManageWebtoonResDto>> getWebtoons (@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(manageWebtoonService.getWebtoons(pageable));
    }
}
