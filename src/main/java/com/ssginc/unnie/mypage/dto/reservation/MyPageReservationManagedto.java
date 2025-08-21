package com.ssginc.unnie.mypage.dto.reservation;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MyPageReservationManagedto {
    private Long reservationId;
    private String memberName;
    private String procedureName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long designerId;
//    private int procedureTime;
}