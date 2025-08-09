package com.ssginc.unnie.payment.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TossWidgetSessionResponse {
    private String clientKey;
    private String orderId;
    private Integer amount;
    private String orderName;
    private String successUrl;
    private String failUrl;
}
