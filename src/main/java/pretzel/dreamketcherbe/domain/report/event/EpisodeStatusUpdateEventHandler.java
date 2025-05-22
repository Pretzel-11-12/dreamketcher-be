package pretzel.dreamketcherbe.domain.report.event;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class EpisodeStatusUpdateEventHandler {

    private final EpisodeRepository episodeRepository;

    /**
     * 에피소드 상태 전환 - NORMAL
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleResolvedEpisode(EpisodeStatusUpdateEvent event) {
        try {
            Optional<Episode> episodeOptional = episodeRepository.findReportedEpisodeById(
                event.getEpisodeId());

            if (episodeOptional.isEmpty()) {
                log.warn("에피소드 상태 전환 실패 - 에피소드 신고 존재하지 않음, episodeId: {}, reportId: {}",
                    event.getEpisodeId(), event.getReportId());
                return;
            }

            Episode episode = episodeOptional.get();
            episode.normalize();
            episodeRepository.save(episode);

            log.info("에피소드 상태 전환 완료 - NORMAL, episodeId: {}, processedBy: {}, processedAt: {}",
                event.getEpisodeId(), event.getProcessedBy(), event.getProcessedAt());
        } catch (Exception e) {
            log.error("에피소드 상태 전환 실패 - 에러 발생, episodeId: {}, reportedId: {}, error: {}",
                event.getEpisodeId(), event.getReportId(), e.getMessage(), e);
            throw e;
        }
    }

}
