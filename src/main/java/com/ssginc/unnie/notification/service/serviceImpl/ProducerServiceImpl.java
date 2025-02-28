package com.ssginc.unnie.notification.service.serviceImpl;

import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.mapper.NotificationMapper;
import com.ssginc.unnie.notification.service.ProducerService;
import com.ssginc.unnie.notification.vo.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {
    private final NotificationMapper notificationMapper;

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    public void createNotification(NotificationMessage message) {
        log.info("알림 생성 ={}", message);
        kafkaTemplate.send("notification", message);
    }

}
