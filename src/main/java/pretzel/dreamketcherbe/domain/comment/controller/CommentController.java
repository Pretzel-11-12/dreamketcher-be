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
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentNotRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentRecommendationResDto;
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
        @PathVariable Long episodeId,
        @RequestBody @Valid CreateCommentReqDto request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.createComment(memberId, episodeId, request));
    }

    /**
     * 댓글 삭제
     */
    @PostMapping("/{commentId}/delete")
    public ResponseEntity<Void> deleteComment(@Auth Long memberId, @PathVariable Long episodeId,
        @PathVariable Long commentId) {
        commentService.deleteComment(memberId, commentId);

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
     * 대댓글 생성
     */
    @PostMapping("/{commentId}/recomment/create")
    public ResponseEntity<CreateRecommentResDto> createRecomment(@Auth Long memberId,
        @PathVariable Long episodeId, @PathVariable Long commentId,
        @RequestBody @Valid CreateRecommentReqDto request) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.createRecomment(memberId, episodeId, commentId, request));
    }

    /**
     * 대댓글 삭제
     */
    @PostMapping("/{commentId}/recomment/{recommentId}/delete")
    public ResponseEntity<Void> deleteRecomment(@Auth Long memberId, @PathVariable Long episodeId,
        @PathVariable Long commentId, @PathVariable Long recommentId) {
        commentService.deleteRecomment(memberId, commentId, recommentId);

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
        commentService.recommendComment(memberId, commentId);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.recommendComment(memberId, commentId));
    }

    /**
     * 댓글 비추천
     */
    @PostMapping("/{commentId}/not-recommend")
    public ResponseEntity<NotRecommendationResDto> notRecommend(@Auth Long memberId,
        @PathVariable Long commentId) {
        commentService.notRecommendComment(memberId, commentId);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.notRecommendComment(memberId, commentId));
    }

    /**
     * 댓글 추천 해제
     */
    @DeleteMapping("/{commentId}/recommend")
    public ResponseEntity<Void> unrecommend(@Auth Long memberId, @PathVariable Long commentId) {
        commentService.unrecommendComment(memberId, commentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 비추천 해제
     */
    @DeleteMapping("/{commentId}/not-recommend")
    public ResponseEntity<Void> unnotRecommend(@Auth Long memberId, @PathVariable Long commentId) {
        commentService.unnotRecommendComment(memberId, commentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 답글 추천
     */
    @PostMapping("/{commentId}/recomments/{recommentId}/recommend")
    public ResponseEntity<CreateRecommentRecommendationResDto> recommentRecommendation(
        @Auth Long memberId,
        @PathVariable Long episodeId, @PathVariable Long commentId,
        @PathVariable Long recommentId) {
        CreateRecommentRecommendationResDto response = commentService.recommendRecomment(memberId,
            recommentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 답글 추천 해제
     */
    @DeleteMapping("/{commentId}/recomment/{recommentId}/recommend")
    public ResponseEntity<Void> cancelRecommendRecomment(@Auth Long memberId,
        @PathVariable Long episodeId, @PathVariable Long commentId,
        @PathVariable Long recommentId, @PathVariable Long recommendationId) {
        commentService.unrecommentRecommendation(memberId, recommentId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 답글 비추천
     */
    @PostMapping("/{commentId}/recomment/{recommentId}/not-recommend")
    public ResponseEntity<CreateRecommentNotRecommendationResDto> recommentNotRecommendation(
        @Auth Long memberId,
        @PathVariable Long episodeId, @PathVariable Long commentId,
        @PathVariable Long recommentId) {
        CreateRecommentNotRecommendationResDto response = commentService.recommentNotRecommendation(
            memberId, recommentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 답글 비추천 해제
     */
    @DeleteMapping("/{commentId}/recomment/{recommentId}/not-recommend")
    public ResponseEntity<Void> cancelRecommentNotRecommend(@Auth Long memberId,
        @PathVariable Long episodeId, @PathVariable Long commentId,
        @PathVariable Long recommentId) {
        commentService.unrecommentNotRecommendation(memberId, recommentId);

        return ResponseEntity.noContent().build();
    }

}
