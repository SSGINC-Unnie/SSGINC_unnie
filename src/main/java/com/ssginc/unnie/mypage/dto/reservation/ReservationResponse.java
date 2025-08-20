package com.ssginc.unnie.mypage.dto.reservation;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationResponse {
    private Long reservationId;
    private String shopName;
    private String procedureName;
    private String designerName;
    private LocalDateTime reservationTime;
    private String status;
    private int price;
}