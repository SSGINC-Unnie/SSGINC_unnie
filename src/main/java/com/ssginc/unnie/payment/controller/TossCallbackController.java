package com.ssginc.unnie.payment.controller;

import com.ssginc.unnie.payment.dto.PaymentApproveRequest;
import com.ssginc.unnie.payment.pg.TossGateway;
import com.ssginc.unnie.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller; // 1. @RestController -> @Controller로 변경
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller // API가 아닌 페이지 이동을 담당하므로 @Controller로 변경
@RequestMapping("/api/payments/toss")
@RequiredArgsConstructor
public class TossCallbackController {

    @Value("${pg.mode:local}")
    private String pgMode;

    private final TossGateway tossGateway;
    private final PaymentService paymentService;

    @GetMapping("/success")
    public String success(
                           @RequestParam String paymentKey,
                           @RequestParam String orderId,
                           @RequestParam Long amount
    ) {
        log.info("Toss success: paymentKey={}, orderId={}, amount={}, mode={}", paymentKey, orderId, amount, pgMode);
        try {
            tossGateway.confirm(paymentKey, orderId, amount);
            paymentService.approve(new PaymentApproveRequest("TOSS", orderId, paymentKey));

            return "redirect:/reservation/complete?orderId=" + orderId;

        } catch (Exception e) {
            log.error("결제 승인 처리 중 오류 발생: {}", e.getMessage());
            return "redirect:/reservation/fail?message=" + e.getMessage();
        }
    }

    @GetMapping("/fail")
    public String fail( // 반환 타입을 String으로 변경
                        @RequestParam String code,
                        @RequestParam String message,
                        @RequestParam(required = false) String orderId
    ) {
        return "redirect:/reservation/fail?message=" + message;
    }
}