package com.ssginc.unnie.notification.mapper;

import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.vo.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {
    long insertNotification(NotificationMessage notification);

    List<NotificationMessage> getMyNotificationsByMemberId(long memberId);

    List<Notification> getAllMyNotificationsByMemberId(long memberId);

    void updateIsRead(long notificationId);

    void updateAllIsReadByMemberId(long memberId);

    int countUnreadNotifications(long memberId);

}
