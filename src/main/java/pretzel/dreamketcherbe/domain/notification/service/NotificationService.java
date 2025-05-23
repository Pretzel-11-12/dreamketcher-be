package pretzel.dreamketcherbe.domain.notification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, Set<SseEmitter>> emitterSets = new ConcurrentHashMap<>();

    /**
     * 연결 생성
     */
    public SseEmitter createConnection(Long memberId) {
        SseEmitter emitter = new SseEmitter(30L * 60 * 1000); // 30분

        emitterSets.computeIfAbsent(memberId, k -> ConcurrentHashMap.newKeySet()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(memberId, emitter));
        emitter.onTimeout(() -> removeEmitter(memberId, emitter));
        emitter.onError((e) -> removeEmitter(memberId, emitter));

        try {
            emitter.send(SseEmitter.event()
                .name("connect")
                .data("연결되었습니다."));
        } catch (Exception e) {
            removeEmitter(memberId, emitter);
        }
        return emitter;
    }

    /**
     * 연결 종료
     *
     * @param memberId
     * @param emitter
     */
    private void removeEmitter(Long memberId, SseEmitter emitter) {
        Set<SseEmitter> emitters = emitterSets.get(memberId);

        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                emitterSets.remove(memberId);
            }
        }
    }

    /**
     * 알림 전송
     */
    public void sendNotification(Long memberId, String message) {
        Set<SseEmitter> emitters = emitterSets.get(memberId);

        if (emitters.isEmpty() || emitters == null) {
            log.info("{}, 연결이 존재하지 않습니다.", memberId);
            return;
        }

        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(message));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }
        deadEmitters.forEach(emitter -> removeEmitter(memberId, emitter));
    }
}