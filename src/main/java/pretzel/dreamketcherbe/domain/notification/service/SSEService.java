package pretzel.dreamketcherbe.domain.notification.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SSEService {

    private final Map<Long, Set<SseEmitter>> emitterSets = new ConcurrentHashMap<>();

    private static final Long SSE_TIMEOUT = 30L * 60 * 1000;

    /**
     * 연결 생성
     */
    public SseEmitter createConnection(Long memberId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        setupEmitterEventHandlers(memberId, emitter);

        addEmitter(memberId, emitter);

        if (!sendConnectionSuccessMessage(emitter)) {
            removeEmitter(memberId, emitter);
            return null; // 연결 실패 시 null 반환
        }

        log.info("SSE 연결 생성 성공 - 사용자: {}, 현재 연결 수: {}",
            memberId, getConnectionCount(memberId));

        return emitter;
    }

    /**
     * Emitter 이벤트 핸들러 설정
     */
    private void setupEmitterEventHandlers(Long memberId, SseEmitter emitter) {
        emitter.onCompletion(() -> {
            removeEmitter(memberId, emitter);
            log.debug("SSE 연결 완료 - 사용자: {}", memberId);
        });

        emitter.onTimeout(() -> {
            removeEmitter(memberId, emitter);
            log.debug("SSE 연결 타임아웃 - 사용자: {}", memberId);
        });

        emitter.onError((e) -> {
            removeEmitter(memberId, emitter);
            log.warn("SSE 연결 에러 - 사용자: {}, 에러: {}", memberId, e.getMessage());
        });
    }

    /**
     * 연결 풀에 Emitter 추가
     */
    private void addEmitter(Long memberId, SseEmitter emitter) {
        emitterSets.computeIfAbsent(memberId, k -> ConcurrentHashMap.newKeySet()).add(emitter);
    }

    /**
     * 연결 성공 메시지 전송
     */
    private boolean sendConnectionSuccessMessage(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event()
                .name("connect")
                .data(createConnectionMessage()));
            return true;
        } catch (IOException e) {
            log.warn("SSE 연결 성공 메시지 전송 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 연결 성공 메시지 생성
     */
    private String createConnectionMessage() {
        return String.format("{\"message\":\"연결되었습니다.\",\"timestamp\":\"%s\"}",
            LocalDateTime.now());
    }

    /**
     * 알림 전송
     */
    public void sendNotification(Long memberId, String message) {
        Set<SseEmitter> emitters = emitterSets.get(memberId);

        if (emitters == null || emitters.isEmpty()) {
            log.debug("SSE 연결이 존재하지 않습니다 - 사용자: {}", memberId);
            return;
        }

        List<SseEmitter> deadEmitters = new ArrayList<>();
        int successCount = 0;
        int totalEmitters = emitters.size();

        for (SseEmitter emitter : emitters) {
            if (sendNotificationToEmitter(emitter, message)) {
                successCount++;
            } else {
                deadEmitters.add(emitter);
            }
        }

        // 실패한 연결 정리
        deadEmitters.forEach(emitter -> removeEmitter(memberId, emitter));

        boolean isSuccess = successCount > 0;
        log.info("SSE 알림 전송 완료 - 사용자: {}, 성공: {}/{}, 실패: {}",
            memberId, successCount, totalEmitters, deadEmitters.size());

    }

    /**
     * 개별 Emitter에 알림 전송
     */
    private boolean sendNotificationToEmitter(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event()
                .name("notification")
                .data(message));
            return true;
        } catch (IOException e) {
            log.debug("SSE 알림 전송 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Emitter 제거
     */
    private void removeEmitter(Long memberId, SseEmitter emitter) {
        Set<SseEmitter> emitters = emitterSets.get(memberId);

        if (emitters != null) {
            emitters.remove(emitter);

            if (emitters.isEmpty()) {
                emitterSets.remove(memberId);
                log.info("사용자의 모든 SSE 연결 제거됨 - 사용자: {}", memberId);
            }
        }

        try {
            emitter.complete();
        } catch (Exception e) {
            log.debug("Emitter 완료 처리 중 에러: {}", e.getMessage());
        }
    }

    /**
     * 특정 사용자의 연결 수 조회
     */
    public int getConnectionCount(Long memberId) {
        Set<SseEmitter> emitters = emitterSets.get(memberId);
        return emitters != null ? emitters.size() : 0;
    }

    /**
     * 전체 연결 수 조회
     */
    public int getTotalConnectionCount() {
        return emitterSets.values().stream()
            .mapToInt(Set::size)
            .sum();
    }

    /**
     * 현재 연결된 사용자 수 조회
     */
    public int getConnectedMemberCount() {
        return emitterSets.size();
    }
}