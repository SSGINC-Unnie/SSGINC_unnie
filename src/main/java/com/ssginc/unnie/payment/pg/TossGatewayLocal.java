package com.ssginc.unnie.payment.pg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "pg.mode", havingValue = "local", matchIfMissing = true)
public class TossGatewayLocal implements TossGateway {
    @Override
    public void confirm(String paymentKey, String orderId, Long amount) {
        log.info("[LOCAL] skip confirm. paymentKey={}, orderId={}, amount={}", paymentKey, orderId, amount);

    }
}
