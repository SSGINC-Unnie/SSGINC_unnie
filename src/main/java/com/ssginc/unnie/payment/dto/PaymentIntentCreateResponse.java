package com.ssginc.unnie.payment.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder
public class PaymentIntentCreateResponse {
    private Long intentId;
    private String orderId;
    private String redirectUrl;
    private Integer amount;
    private String orderName;
}