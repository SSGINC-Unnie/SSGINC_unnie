package com.ssginc.unnie.notification.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 알림 VO 클래스
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Notification {
    long notificationId; // 알림번호
    NotificationType notificationType; // 알림타입
    String notificationContents; // 알림 내용
    LocalDateTime notificationCreatedAt; // 알림 발생일시
    String notificationUrn; // 알림 발생 장소
    boolean notificationIsRead; // 알림 확인 여부
    long notificationMemberId; // 수신자 식별 번호
}
