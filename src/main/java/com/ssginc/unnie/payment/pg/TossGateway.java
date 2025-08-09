package com.ssginc.unnie.payment.pg;
public interface TossGateway {
    void confirm(String paymentKey, String orderId, Long amount);

    }
