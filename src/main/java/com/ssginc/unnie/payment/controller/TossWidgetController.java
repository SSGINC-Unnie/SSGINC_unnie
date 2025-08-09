package com.ssginc.unnie.payment.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.payment.dto.PaymentIntentCreateRequest;
import com.ssginc.unnie.payment.dto.PaymentIntentCreateResponse;
import com.ssginc.unnie.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/toss")
@RequiredArgsConstructor
public class TossWidgetController {

    private final PaymentService paymentService;

    @Value("${toss.client-key}")
    private String tossClientKey;
    @Value("${toss.success-url}")
    private String successUrl;
    @Value("${toss.fail-url}")
    private String failUrl;

    @PostMapping("/widget-session")
    public ResponseEntity<ResponseDto<?>> createSession(@RequestBody PaymentIntentCreateRequest req) {
        req.setProvider("TOSS");
        String orderId = req.getOrderId();

        PaymentIntentCreateResponse intent = paymentService.createIntent(req);
        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "세션 생성 성공",
                        Map.of(
                                "clientKey",  tossClientKey,
                                "orderId",    intent.getOrderId(),
                                "amount",     intent.getAmount(),
                                "orderName",  intent.getOrderName(),
                                "successUrl", successUrl,
                                "failUrl",    failUrl
                        )
                )
        );
    }
}
