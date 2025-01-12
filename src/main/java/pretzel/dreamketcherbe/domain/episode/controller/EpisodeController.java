package pretzel.dreamketcherbe.domain.episode.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeLikeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.EpisodeResDto;
import pretzel.dreamketcherbe.domain.episode.dto.EpisodeStarResDto;
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
    public ResponseEntity<WebtoonEpisodeListResDto> getEpisodes(
        @PathVariable Long webtoonId,
        @RequestParam(defaultValue = "false") boolean fromFirst,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        WebtoonEpisodeListResDto result = episodeService.getWebtoonEpisodes(
            webtoonId, fromFirst, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 에피소드 등록
     */
    @PostMapping("/uploads")
    public ResponseEntity<CreateEpisodeResDto> createEpisode(@Auth Long memberId,
        @PathVariable("webtoonId") Long webtoonId,
        @RequestBody @Valid CreateEpisodeReqDto request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(episodeService.createEpisode(memberId, webtoonId, request));
    }

    /**
     * 에피소드 썸네일 등록
     */
    @PostMapping("/thumbnail")
    public ResponseEntity<String> uploadEpisodeThumbnail(@Auth Long memberId,
        @PathVariable Long webtoonId,
        @RequestParam("thumbnail") MultipartFile thumbnail) {
        String thumbnailUrl = episodeService.uploadThumbnail(webtoonId, memberId, thumbnail);

        return ResponseEntity.ok(thumbnailUrl);
    }

    /**
     * 에피소드 썸네일 수정
     */
    @PutMapping("/{episodeId}/thumbnail")
    public ResponseEntity<String> updateEpisodeThumbnail(@Auth Long memberId,
        @PathVariable("episodeId") Long episodeId,
        @RequestParam("oldThumbnail") String oldThumbnail,
        @RequestParam("newThumbnail") MultipartFile newThumbnail,
        @RequestParam("folderName") String folderName) {
        String thumbnailUrl = episodeService.updateThumbnail(oldThumbnail, newThumbnail,
            folderName);

        return ResponseEntity.ok(thumbnailUrl);
    }

    /**
     * 에피소드 컨텐츠 등록
     */
    @PostMapping("/content")
    public ResponseEntity<String> uploadEpisodeContent(@Auth Long memberId,
        @PathVariable Long webtoonId,
        @RequestParam("content") List<MultipartFile> content, ObjectMapper objectMapper) {
        String contentUrl = episodeService.uploadContent(webtoonId, memberId, content,
            objectMapper);

        return ResponseEntity.ok(contentUrl);
    }

    /**
     * 에피소드 컨텐츠 수정
     */
    @PutMapping("/{episodeId}/content")
    public ResponseEntity<String> updateEpisodeContent(@Auth Long memberId,
        @PathVariable("episodeId") Long episodeId,
        @RequestParam("existingUrls") String existingUrls,
        @RequestParam("newImages") List<MultipartFile> newImages,
        @RequestParam("replaceIndices") List<Integer> replaceIndices,
        @RequestParam("folderName") String folderName,
        ObjectMapper objectMapper) {
        String updatedContentUrls = episodeService.updateContent(existingUrls, newImages,
            replaceIndices, folderName, objectMapper);

        return ResponseEntity.ok(updatedContentUrls);
    }


    /**
     * 에피소드 수정
     */
    @PutMapping("/{episodeId}")
    public ResponseEntity<Void> updateEpisode(@Auth Long memberId,
        @PathVariable("webtoonId") Long webtoonId,
        @PathVariable("episodeId") Long episodeId,
        @RequestBody @Valid UpdateEpisodeReqDto request) throws JsonProcessingException {
        episodeService.updateEpisode(memberId, webtoonId, episodeId, request);

        return ResponseEntity.ok().build();
    }

    /**
     * 에피소드 삭제
     */
    @DeleteMapping("/{episodeId}")
    public ResponseEntity<Void> deleteEpisode(@Auth Long memberId,
        @PathVariable("webtoonId") Long webtoonId,
        @PathVariable("episodeId") Long episodeId) {
        episodeService.deleteEpisode(memberId, webtoonId, episodeId);

        return ResponseEntity.ok().build();
    }

    /**
     * 에피소드 상세 조회
     */
    @GetMapping("/{episodeId}")
    public ResponseEntity<EpisodeResDto> getEpisode(@PathVariable("episodeId") Long episodeId,
        @PathVariable("webtoonId") Long webtoonId
        , Model model,
        HttpServletRequest request, HttpServletResponse response) {
        EpisodeResDto episode = episodeService.getEpisode(webtoonId, episodeId, request, response);

        return ResponseEntity.ok(episode);
    }

    /**
     * 에피소드 별점 등록
     */
    @PutMapping("/{episodeId}/star")
    public ResponseEntity<EpisodeStarResDto> starEpisode(@Auth Long memberId,
        @PathVariable("webtoonId") Long webtoonId,
        @PathVariable("episodeId") Long episodeId,
        @RequestParam @Min(0) @Max(5) float point) {

        EpisodeStarResDto episodeStar = episodeService.starEpisode(memberId, webtoonId, episodeId,
            point);

        return ResponseEntity.ok(episodeStar);
    }

    /**
     * 에피소드 별점 삭제
     */
    @DeleteMapping("/{episodeId}/star")
    public ResponseEntity<Void> deleteStarEpisode(@Auth Long memberId,
        @PathVariable("webtoonId") Long webtoonId,
        @PathVariable("episodeId") Long episodeId) {
        episodeService.deleteEpisodeStar(memberId, webtoonId, episodeId);

        return ResponseEntity.ok().build();
    }

    /**
     * 에피소드 좋아요
     */
    @PostMapping("/{episodeId}/like")
    public ResponseEntity<CreateEpisodeLikeResDto> likeEpisode(@Auth Long memberId,
        @PathVariable("webtoonId") Long webtoonId,
        @PathVariable("episodeId") Long episodeId) {
        CreateEpisodeLikeResDto like = episodeService.likeEpisode(webtoonId, episodeId, memberId);

        return ResponseEntity.ok(like);
    }

    /**
     * 좋아요 수 가져오기
     */
    @GetMapping("/{episodeId}/like-count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable("episodeId") Long episodeId) {
        try {
            String likeCountKey = EpisodeService.EPISODE_LIKE_COUNT_KEY_PREFIX + episodeId;
            String likeCount = episodeService.redisTemplate.opsForValue().get(likeCountKey);
            int likeCountInt = likeCount == null ? episodeService.getLikeCountFallback(episodeId)
                : Integer.parseInt(likeCount);
            return ResponseEntity.ok(likeCountInt);
        } catch (Exception e) {
            int fallbackCount = episodeService.getLikeCountFallback(episodeId);
            return ResponseEntity.ok(fallbackCount);
        }
    }
}
