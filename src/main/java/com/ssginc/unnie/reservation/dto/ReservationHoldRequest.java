package com.ssginc.unnie.reservation.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationHoldRequest {
    private Long memberId;
    private Integer shopId;
    private Integer designerId;  // nullable 가능
    private Integer procedureId;
    private LocalDateTime startTime;
    private Integer reservationId;
    // 기본 10분 홀드
    private Integer holdMinutes = 10;
}
