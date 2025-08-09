package com.ssginc.unnie.payment.dto;
import lombok.Data;

@Data
public class TossWidgetSessionRequest {
    private Long reservationId;
    private Long memberId;
    private Integer shopId;
    private String orderId;
}
