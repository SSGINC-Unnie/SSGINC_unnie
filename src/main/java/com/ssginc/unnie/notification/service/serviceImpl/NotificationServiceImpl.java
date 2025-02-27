package com.ssginc.unnie.notification.service.serviceImpl;

import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.mapper.NotificationMapper;
import com.ssginc.unnie.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;


    @Override
    public long insertNotification(NotificationMessage notification) {

        return notificationMapper.insertNotification(notification);
    }
}
