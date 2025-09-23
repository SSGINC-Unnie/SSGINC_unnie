package com.ssginc.unnie.notification.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.common.exception.UnnieNotificationException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.dto.NotificationResponse;
import com.ssginc.unnie.notification.mapper.NotificationMapper;
import com.ssginc.unnie.notification.repository.EmitterRepository;
import com.ssginc.unnie.notification.service.NotificationService;
import com.ssginc.unnie.notification.vo.Notification;
import com.ssginc.unnie.notification.vo.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    // SSE 연결 지속 시간
    private static final long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;

    private final NotificationMapper notificationMapper;

//    @KafkaListener(topics = "notification", groupId = "group_1",
//            containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaSend(NotificationMessage notification){
//        send(notification);
//    }


    @Override
    public long insertNotification(NotificationMessage notification) {
        return notificationMapper.insertNotification(notification);
    }

    /**
     * subscribe 관련 메소드
     * @param id 유저 식별 번호
     * @param lastEventId 마지막 이벤트의 식별 번호
     * @return
     */
    @Override
    public SseEmitter subscribe(long id, String lastEventId) {

        //  SseEmitter 식별 목적의 고유 아이디 생성
        String emitterId = makeTimeIncludeId(id);

        // SseEmitter 객체 생성 및 저장
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        log.info("Emitter 생성 = {}", emitter);

        // onCompletion()(SseEmitter 완료 시) 메서드 사용해 SseEmitter 삭제
        emitter.onCompletion(() -> {
            emitterRepository.deleteById(emitterId);
            log.info("Emitter 삭제: {}", emitterId);
        });

        //onTimeout()(SseEmitter 지속 시간동안 이벤트 미전송 시)
        emitter.onTimeout(() -> {
            emitterRepository.deleteById(emitterId);
            log.info("Emitter 만료: {}", emitterId);
        });

        // 503 에러 방지 목적의 더미 이벤트 전송
        String eventId = makeTimeIncludeId(id);
        sendNotification(emitter, eventId, emitterId, createDummyNotification(id));

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실 예방
        if(lastEventId != null) {
            sendLostData(lastEventId, String.valueOf(id), emitterId, emitter);
        }


        return emitter;
    }

    private void sendLostData(String lastEventId, String memberId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(memberId);
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data, MediaType.APPLICATION_JSON)
            );
            log.info("Notification sent successfully to emitter: {}", emitterId); // 성공 시 로그
        } catch (IOException e) {
            log.error("Emitter 전송 중 IO 예외 발생 = {}", emitterId, e);
            emitterRepository.deleteById(emitterId);
        }
    }

    private NotificationMessage createDummyNotification(long memberId) {
        return NotificationMessage.builder()
                        .notificationMemberId(memberId)
                .notificationType(NotificationType.LIKE)
                .notificationUrn("test")
                .build();
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void send(NotificationMessage notification) {

        log.info("Emitter send 메서드 실행 = {}", notification);

        long res = notificationMapper.insertNotification(notification);

        String receiverId = String.valueOf(notification.getNotificationMemberId());
        String eventId = makeTimeIncludeId(res);

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverId);

        log.info("Emitter 탐색 = {}", emitters.size());

        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, notification);
                }
        );

    }

    @Override
    public List<NotificationMessage> getMyNotificationsByMemberId(long memberId) {

        List<NotificationMessage> res = notificationMapper.getMyNotificationsByMemberId(memberId);

        if (res == null) {
            throw new UnnieNotificationException(ErrorCode.NOTIFICATION_SERVICE_ERROR);
        }

        return res;
    }

    @Override
    public PageInfo<Notification> getAllMyNotificationsByMemberId(long memberId, int page) {
        PageHelper.startPage(page, 10); // 한 페이지에 10개씩 표시
        List<Notification> notifications = notificationMapper.getAllMyNotificationsByMemberId(memberId);
        return new PageInfo<>(notifications);
    }


    /**
     * Emitter 고유 아이디 생성
     */
    private String makeTimeIncludeId(long memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }
}
