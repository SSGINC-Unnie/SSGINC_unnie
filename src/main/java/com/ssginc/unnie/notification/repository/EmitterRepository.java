package com.ssginc.unnie.notification.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {

    // 동시성 문제 해결하고자 thread-safe한 ConcurrentHashMap 사용
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    /**
     * emitter 저장
     */
    public SseEmitter save(String emitterId, SseEmitter emitter) {
        emitters.put(emitterId, emitter);
        return emitter;
    }

    /**
     * 이벤트 저장
     */
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    /**
     * 해당 회원 관련 모든 이벤트 탐색
     */
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * emitter 삭제
     */
    public void deleteById(String id) {
        emitters.remove(id);
    }

    /**
     * 해당 회원 관련 모든 emitter 삭제
     */
    public void deleteAllEmitterStartWithMemberId(String memberId) {
        emitters.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        emitters.remove(key);
                    }
                });
    }

    /**
     * 해당 회원과 관련된 모든 이벤트를 지움
     */
    public void deleteAllEventCacheStartWithMemberId(String memberId) {
        eventCache.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        eventCache.remove(key);
                    }
                });
    }
}
