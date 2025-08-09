package com.ssginc.unnie.reservation.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationHoldRequest {
    private Long memberId;
    private Integer shopId;
    private Integer designerId;  // nullable 가능
    private Integer procedureId;

    // 정시(분=0, 초=0)로 들어오게 해줘 (스키마 CHECK 걸려 있음)
    private LocalDateTime startTime;

    // 기본 10분 홀드
    private Integer holdMinutes = 10;
}
