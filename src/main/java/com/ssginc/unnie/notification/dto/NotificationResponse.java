package com.ssginc.unnie.notification.dto;

import lombok.*;

/**
 * 응답 전용 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private long receiverId;
    private String receiverNickname;
    private String targetTitle;
}
