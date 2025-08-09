package com.ssginc.unnie.payment.dto;
import lombok.Data;

@Data
public class PaymentIntentCreateRequest {
    private Long reservationId;
    private Long memberId;
    private Integer shopId;
    private String provider; // TOSS/KAKAO/NAVER/INICIS
    private String orderId;  // 멱등키
}