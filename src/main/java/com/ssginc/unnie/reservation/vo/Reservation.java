package com.ssginc.unnie.reservation.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * reservation 테이블의 데이터를 담는 VO 클래스
 */
@Data
public class Reservation {

    private Long reservationId;
    private Long memberId;
    private Integer shopId;
    private Integer designerId;
    private Integer procedureId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer peopleCount;
    private String status;
    private LocalDateTime holdExpiresAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}