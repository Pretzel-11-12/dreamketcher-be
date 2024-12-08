package pretzel.dreamketcherbe.domain.episode.controller;

import com.amazonaws.Response;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeResDto;
import pretzel.dreamketcherbe.domain.episode.service.EpisodeService;

@Slf4j
@RestController
@RequestMapping("/api/v1/episodes")
@AllArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;

    /**
     * 에피소드 등록
     */
    @PostMapping
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
        @RequestBody @Valid CreateEpisodeReqDto request) {
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
}
