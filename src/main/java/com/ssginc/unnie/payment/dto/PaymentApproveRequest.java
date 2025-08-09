package com.ssginc.unnie.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentApproveRequest {
    private String provider;    // TOSS / KAKAO / NAVER / INICIS
    private String orderId;     // 결제의도 생성 시 사용한 멱등키
    private String providerTid; // PG 거래번호(테스트에선 임의값 가능)
}
