package com.ssginc.unnie.notification.service;

import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.dto.NotificationResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {

    long insertNotification(NotificationMessage notification);


    SseEmitter subscribe(long memberEmail, String lastEventId);

    void send(NotificationMessage notification);

    List<NotificationMessage> getMyNotificationsByMemberId(long memberId);
}
