package pretzel.dreamketcherbe.domain.report.event;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;
import pretzel.dreamketcherbe.domain.comment.repository.RecommentRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommentStatusUpdateEventHandler {

    private final RecommentRepository recommentRepository;

    /**
     * 답글 상태 전환 - NORMAL
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleResolvedRecomment(RecommentStatusUpdateEvent event) {
        try {
            Optional<Recomment> recommentOptional = recommentRepository.findReportedRecomment(
                event.getRecommentId());

            if (recommentOptional.isEmpty()) {
                log.warn("답글 상태 전환 실패 - 답글 신고 존재하지 않음, recommentId: {}, reportId: {}",
                    event.getRecommentId(), event.getReportId());
                return;
            }

            Recomment recomment = recommentOptional.get();
            recomment.normalize();
            recommentRepository.save(recomment);

            log.info("답글 상태 전환 완료 - NORMAL, recommentId: {}, processedBy: {}, processedAt: {}",
                event.getRecommentId(), event.getProcessedBy(), event.getProcessedAt());
        } catch (Exception e) {
            log.error("답글 상태 전환 실패 - 에러 발생, recommentId: {}, reportId: {}, error: {}",
                event.getRecommentId(), event.getReportId(), e.getMessage(), e);
            throw e;
        }
    }
}
