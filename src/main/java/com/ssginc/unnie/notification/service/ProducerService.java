package com.ssginc.unnie.notification.service;

import com.ssginc.unnie.notification.dto.NotificationMessage;

public interface ProducerService {
    void createNotification(NotificationMessage message);
}
