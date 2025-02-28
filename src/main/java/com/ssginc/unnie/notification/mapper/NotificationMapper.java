package com.ssginc.unnie.notification.mapper;

import com.ssginc.unnie.notification.dto.NotificationMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {
    long insertNotification(NotificationMessage notification);

    List<NotificationMessage> getMyNotificationsByMemberId(long memberId);
}
