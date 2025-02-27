package com.ssginc.unnie.notification.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 응답 전용 DTO
 */
@Getter
@Setter
public class NotificationResponse {
    private long receiverId;
    private String receiverNickname;
    private String targetTitle;
}
