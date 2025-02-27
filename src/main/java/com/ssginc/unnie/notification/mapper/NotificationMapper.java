package com.ssginc.unnie.notification.mapper;

import com.ssginc.unnie.notification.dto.NotificationMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper {
    long insertNotification(NotificationMessage notification);
}
