package com.ssginc.unnie.payment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentSuccess {
    private String orderId;
    private String memberName;
    private String shopName;
    private String procedureName;
    private String designerName;
    private LocalDateTime reservationTime;
    private Integer amount;
}