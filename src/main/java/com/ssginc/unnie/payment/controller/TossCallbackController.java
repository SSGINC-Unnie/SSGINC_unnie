package com.ssginc.unnie.payment.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.payment.dto.PaymentApproveRequest;
import com.ssginc.unnie.payment.pg.TossGateway;
import com.ssginc.unnie.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments/toss")
@RequiredArgsConstructor
public class TossCallbackController {

    @Value("${pg.mode:local}")
    private String pgMode;

    private final TossGateway tossGateway;
    private final PaymentService paymentService;

    @GetMapping("/success")
    public ResponseEntity<ResponseDto<Void>> success(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount
    ) {
        log.info("Toss success params: paymentKey={}, orderId={}, amount={}, mode={}", paymentKey, orderId, amount, pgMode);
        // 현재 pgMode가 무엇인지 로그에 기록
        log.info("현재 PG 모드: {}", pgMode); // 이 줄을 추가
        tossGateway.confirm(paymentKey, orderId, amount);

        // 결제 승인 처리
        paymentService.approve(new PaymentApproveRequest("TOSS", orderId, paymentKey));

        return ResponseEntity.ok(new ResponseDto<>(200, "토스 결제 승인 및 예약 확정 완료", null));
    }

    @GetMapping("/fail")
    public ResponseEntity<ResponseDto<Void>> fail(
            @RequestParam String code,
            @RequestParam String message,
            @RequestParam(required = false) String orderId
    ) {
        log.warn("Toss fail: code={}, message={}, orderId={}", code, message, orderId);
        return ResponseEntity.badRequest().body(new ResponseDto<>(400, "토스 결제 실패: " + code + " - " + message, null));
    }
}
