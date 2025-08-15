package com.ssginc.unnie.payment.pg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "pg.mode", havingValue = "local", matchIfMissing = true)
public class TossGatewayLocal implements TossGateway {


    @Override
    public void confirm(String paymentKey, String orderId, Long amount) {
        // 로컬 환경에서는 실제 결제 확인을 하지 않고 가상의 결제 처리
        log.info("[LOCAL] 결제 확인 요청 (시뮬레이션) : paymentKey={}, orderId={}, amount={}", paymentKey, orderId, amount);

        // 시뮬레이션을 위한 결제 성공 시나리오
        // 예시로 결제 상태를 "SUCCESS"로 가정하고, 응답 데이터를 생성합니다.
        Map<String, Object> response = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount,
                "status", "SUCCESS"
        );

        // 실제 결제 API가 호출되는 프로덕션과는 달리, 로컬에서는 이 데이터를 기반으로 처리
        log.info("[LOCAL] 결제 확인 결과: {}", response);

        // 실제 시스템에서는 결제 확인 후 추가 작업을 하겠지만, 로컬에서는 그냥 완료된 것처럼 처리
        log.info("[LOCAL] 결제 확인 성공: 주문 ID {}의 결제가 정상 처리되었습니다.", orderId);
    }
}
