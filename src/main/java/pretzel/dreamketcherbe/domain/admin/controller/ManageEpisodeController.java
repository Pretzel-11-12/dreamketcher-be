package pretzel.dreamketcherbe.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.domain.admin.dto.ManageEpisodeResDto;
import pretzel.dreamketcherbe.domain.admin.service.ManageEpisodeService;

@RestController
@RequestMapping("/api/v1/admin/webtoons/{webtoonId}/episode")
@RequiredArgsConstructor
public class ManageEpisodeController {

    private final ManageEpisodeService manageEpisodeService;

    /**
     * 에피소드 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<ManageEpisodeResDto>> getEpiSodes(@PathVariable Long webtoonId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(manageEpisodeService.getEpisodes(webtoonId, pageable));
    }
}
