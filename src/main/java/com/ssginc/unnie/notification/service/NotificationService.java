package com.ssginc.unnie.notification.service;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.dto.NotificationResponse;
import com.ssginc.unnie.notification.vo.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {

    long insertNotification(NotificationMessage notification);

    SseEmitter subscribe(long memberEmail, String lastEventId);

    void send(NotificationMessage notification);

    List<NotificationMessage> getMyNotificationsByMemberId(long memberId);

    PageInfo<Notification> getAllMyNotificationsByMemberId(long memberId, int page);

    void markAsRead(long notificationId);

    void markAllAsRead(long memberId);

    int countUnreadNotifications(long memberId);

    void deleteNotifications(List<Long> ids);

    void deleteAllNotifications(long memberId);


}
