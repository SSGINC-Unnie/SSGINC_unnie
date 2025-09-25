package com.ssginc.unnie.notification.dto;

import com.ssginc.unnie.notification.vo.NotificationType;
import lombok.*;

/**
 * 메세지 담을
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    NotificationType notificationType; // 알림타입
    String notificationContents; // 알림 내용
    String notificationUrn; // 알림 발생 장소
    long notificationMemberId; // 수신자 식별 번호
    private long notificationId;          // 알림 고유 ID
    private boolean notificationIsRead;   // 알림 읽음 여부
}
