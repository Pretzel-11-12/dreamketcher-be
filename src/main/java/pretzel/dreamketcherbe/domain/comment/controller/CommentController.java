package pretzel.dreamketcherbe.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentReqDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentResDto;
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
        return ResponseEntity.ok().build();
    }
}
