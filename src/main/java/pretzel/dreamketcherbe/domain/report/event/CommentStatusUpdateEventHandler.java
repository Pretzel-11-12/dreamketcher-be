package pretzel.dreamketcherbe.domain.report.event;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;
import pretzel.dreamketcherbe.domain.comment.repository.CommentRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentStatusUpdateEventHandler {

    private final CommentRepository commentRepository;

    /**
     * 댓글 상태 전환 - NORMAL
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleResolvedComment(CommentStatusUpdateEvent event) {
        try {
            Optional<Comment> commentOptional = commentRepository.findReportedComment(
                event.getCommentId());

            if (commentOptional.isEmpty()) {
                log.warn("댓글 상태 전환 실패 - 댓글 신고 존재하지 않음, commentId: {}, reportId: {}",
                    event.getCommentId(), event.getReportId());
                return;
            }

            Comment comment = commentOptional.get();
            comment.normalize();
            commentRepository.save(comment);

            log.info("댓글 상태 전환 완료 - NORMAL, commentId: {}, processedBy: {}, processedAt: {}",
                event.getCommentId(), event.getProcessedBy(), event.getProcessedAt());
        } catch (Exception e) {
            log.error("댓글 상태 전환 실패 - 에러 발생, commentId: {}, reportedId: {}, error: {}",
                event.getCommentId(), event.getReportId(), e.getMessage(), e);
            throw e;
        }
    }


}
