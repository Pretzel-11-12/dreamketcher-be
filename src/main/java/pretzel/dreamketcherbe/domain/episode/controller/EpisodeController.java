package pretzel.dreamketcherbe.domain.episode.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeLikeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.EpisodeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.UpdateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.WebtoonEpisodeListResDto;
import pretzel.dreamketcherbe.domain.episode.service.EpisodeService;

@Slf4j
@RestController
@RequestMapping("/api/v1/webtoons/{webtoonId}/episode")
@AllArgsConstructor
public class EpisodeController {


    private final EpisodeService episodeService;

    /**
     * 에피소드 목록 조회
     */
    @GetMapping
    public ResponseEntity<WebtoonEpisodeListResDto> getEpisodes(@PathVariable Long webtoonId,
        @RequestParam(defaultValue = "false") boolean fromFirst,
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        WebtoonEpisodeListResDto result = episodeService.getWebtoonEpisodes(webtoonId, fromFirst,
            page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 에피소드 등록
     */
    @PostMapping("/uploads")
    public ResponseEntity<CreateEpisodeResDto> createEpisode(@Auth Long memberId,
        @RequestBody @Valid CreateEpisodeReqDto request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(episodeService.createEpisode(memberId, request));
    }

    /**
     * 에피소드 수정
     */
    @PutMapping("/{episodeId}")
    public ResponseEntity<Void> updateEpisode(@Auth Long memberId,
        @PathVariable("episodeId") Long episodeId,
        @RequestBody @Valid UpdateEpisodeReqDto request) {
        episodeService.updateEpisode(memberId, episodeId, request);

        return ResponseEntity.ok().build();
    }

    /**
     * 에피소드 삭제
     */
    @DeleteMapping("/{episodeId}")
    public ResponseEntity<Void> deleteEpisode(@Auth Long memberId,
        @PathVariable("episodeId") Long episodeId) {
        episodeService.deleteEpisode(memberId, episodeId);

        return ResponseEntity.ok().build();
    }

    /**
     * 에피소드 상세 조회
     */
    @GetMapping("/{episodeId}")
    public String getEpisode(@PathVariable("episodeId") Long episodeId, Model model,
        HttpServletRequest request, HttpServletResponse response) {
        EpisodeResDto episode = episodeService.getEpisode(episodeId, request, response);
        episodeService.increaseViewCount(episodeId);
        model.addAttribute("episode", episode);

        return "episode-view";
    }

    /**
     * 에피소드 좋아요
     */
    @PostMapping("/{episodeId}/like")
    public ResponseEntity<CreateEpisodeLikeResDto> likeEpisode(@Auth Long memberId,
        @PathVariable("episodeId") Long episodeId) {
        int likeCount = episodeService.likeEpisode(memberId, episodeId);

        return ResponseEntity
            .status(HttpStatus.CREATED)  // 상태 코드 변경
            .body(CreateEpisodeLikeResDto.of(episodeId, likeCount));
    }
}
