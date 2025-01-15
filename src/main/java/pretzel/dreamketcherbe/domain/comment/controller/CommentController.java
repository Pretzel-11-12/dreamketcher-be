package pretzel.dreamketcherbe.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CommentResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentReqDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentReqDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentResDto;
import pretzel.dreamketcherbe.domain.comment.dto.NotRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.dto.RecommentResDto;
import pretzel.dreamketcherbe.domain.comment.service.CommentService;

@Slf4j
@RestController
@RequestMapping("/api/v1/webtoons/{webtoonId}/episode/{episodeId}/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성
     *
     * @param memberId
     * @param episodeId
     * @param request
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<CreateCommentResDto> createComment(@Auth Long memberId,
        @PathVariable Long webtoonId,
        @PathVariable Long episodeId,
        @RequestBody @Valid CreateCommentReqDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.createComment(memberId, webtoonId, episodeId, request));
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<Void> deleteComment(@Auth Long memberId, @PathVariable Long episodeId,
        @PathVariable Long commentId) {
        commentService.deleteComment(memberId, episodeId, commentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 목록 조회
     */
    @GetMapping
    public ResponseEntity<PageResDto<CommentResDto>> getComments(@PathVariable Long episodeId,
        @RequestParam int page,
        @RequestParam int size, @RequestParam(defaultValue = "DESC") String order) {

        PageReqDto pageReqDto = PageReqDto.of(null, page, size, order);

        PageResDto<CommentResDto> response = commentService.getComments(episodeId, pageReqDto);

        return ResponseEntity.ok(response);
    }

    /**
     * 답글 생성
     */
    @PostMapping("/{commentId}/recomment/create")
    public ResponseEntity<CreateRecommentResDto> createRecomment(@Auth Long memberId,
        @PathVariable Long webtoonId,
        @PathVariable Long episodeId, @PathVariable Long commentId,
        @RequestBody @Valid CreateRecommentReqDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                commentService.createRecomment(memberId, webtoonId, episodeId, commentId, request));
    }

    /**
     * 답글 삭제
     */
    @DeleteMapping("/{commentId}/recomment/{recommentId}/delete")
    public ResponseEntity<Void> deleteRecomment(@Auth Long memberId, @PathVariable Long episodeId,
        @PathVariable Long commentId, @PathVariable Long recommentId) {
        commentService.deleteRecomment(memberId, episodeId, commentId, recommentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 대댓글 목록 조회
     */
    @GetMapping("/{commentId}/recomments")
    public ResponseEntity<PageResDto<RecommentResDto>> getRecomments(
        @PathVariable Long episodeId,
        @PathVariable Long commentId,
        @RequestParam int page,
        @RequestParam int size,
        @RequestParam(defaultValue = "DESC") String order) {

        PageReqDto pageReqDto = PageReqDto.of(null, page, size, order);

        PageResDto<RecommentResDto> response = commentService.getRecomments(episodeId, commentId,
            pageReqDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 추천
     */
    @PostMapping("/{commentId}/recommend")
    public ResponseEntity<CreateRecommendationResDto> recommend(@Auth Long memberId,
        @PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.recommendComment(memberId, commentId));
    }

    /**
     * 댓글 비추천
     */
    @PostMapping("/{commentId}/not-recommend")
    public ResponseEntity<NotRecommendationResDto> notRecommend(@Auth Long memberId,
        @PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.notRecommendComment(memberId, commentId));
    }


    /**
     * 댓글 추천 해제
     */
    @DeleteMapping("/{commentId}/recommend")
    public ResponseEntity<Integer> unrecommend(@Auth Long memberId, @PathVariable Long commentId) {
        int updatedRecommendationCount = commentService.unrecommendComment(memberId, commentId);
        return ResponseEntity.ok(updatedRecommendationCount);
    }

    /**
     * 댓글 비추천 해제
     */
    @DeleteMapping("/{commentId}/not-recommend")
    public ResponseEntity<Integer> unnotRecommend(@Auth Long memberId,
        @PathVariable Long commentId) {
        int updatedNotRecommendationCount = commentService.unnotRecommendComment(memberId,
            commentId);
        return ResponseEntity.ok(updatedNotRecommendationCount);
    }
}
