package pretzel.dreamketcherbe.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentReqDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentNotRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentReqDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentResDto;
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
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteComment(@Auth Long memberId, @PathVariable Long episodeId,
        @PathVariable Long commentId) {
        commentService.deleteComment(memberId, episodeId, commentId);

        return ResponseEntity.noContent().build();
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
    @PostMapping("/{commentId}/recomment/delete")
    public ResponseEntity<Void> deleteRecomment(@Auth Long memberId, @PathVariable Long episodeId,
        @PathVariable Long commentId, @PathVariable Long recommentId) {
        commentService.deleteRecomment(memberId, episodeId, commentId, recommentId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 추천
     */
    @PostMapping("/{commentId}/recommend")
    public ResponseEntity<CreateRecommendationResDto> recommend(@Auth Long memberId,
        @PathVariable Long episodeId,
        @PathVariable Long commentId) {
        commentService.recommendComment(memberId, commentId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 댓글 비추천
     */
    @PostMapping("/{commentId}/not-recommend")
    public ResponseEntity<Void> notRecommend(@Auth Long memberId, @PathVariable Long episodeId,
        @PathVariable Long commentId) {
        commentService.notRecommendComment(memberId, commentId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 추천 해제
     */
    @DeleteMapping("/{commentId}/{recommendationId}/")
    public ResponseEntity<Void> cancelRecommend(@Auth Long memberId, @PathVariable Long episodeId,
        @PathVariable Long commentId, @PathVariable Long recommendationId) {
        commentService.unrecommendComment(memberId, commentId, recommendationId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 비추천 해제
     */
    @DeleteMapping("/{commentId}/{notRecommendationId}/")
    public ResponseEntity<Void> cancelNotRecommend(@Auth Long memberId,
        @PathVariable Long episodeId,
        @PathVariable Long commentId, @PathVariable Long notRecommendationId) {
        commentService.unnotRecommendComment(memberId, commentId, notRecommendationId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 답글 추천
     */
    @PostMapping("/{commentId}/recomment/{recommentId}/recommend")
    public ResponseEntity<CreateRecommendationResDto> recommentRecommendation(@Auth Long memberId,
        @PathVariable Long episodeId, @PathVariable Long commentId,
        @PathVariable Long recommentId) {
        commentService.recommendRecomment(memberId, recommentId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 답글 추천 해제
     */
    @DeleteMapping("/{commentId}/recomment/{recommentId}/{recommentRecommendationId}/")
    public ResponseEntity<Void> cancelRecommentRecommend(@Auth Long memberId,
        @PathVariable Long episodeId, @PathVariable Long commentId,
        @PathVariable Long recommentId, @PathVariable Long recommendationId) {
        commentService.unrecommentRecommendation(memberId, recommentId, recommendationId);

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
        commentService.recommentNotRecommendation(memberId, recommentId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 답글 비추천 해제
     */
    @DeleteMapping("/{commentId}/recomment/{recommentId}/{notRecommentRecommendationId}/")
    public ResponseEntity<Void> cancelRecommentNotRecommend(@Auth Long memberId,
        @PathVariable Long episodeId, @PathVariable Long commentId,
        @PathVariable Long recommentId, @PathVariable Long recommentNotRecommendationId) {
        commentService.unrecommentNotRecommendation(memberId, recommentId,
            recommentNotRecommendationId);

        return ResponseEntity.noContent().build();
    }
}
