package com.ssginc.unnie.payment.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.payment.dto.PaymentIntentCreateRequest;
import com.ssginc.unnie.payment.dto.PaymentIntentCreateResponse;
import com.ssginc.unnie.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
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
    public ResponseEntity<ResponseDto<?>> createSession(
            @RequestBody PaymentIntentCreateRequest req,
            @AuthenticationPrincipal MemberPrincipal principal) {
        req.setMemberId(principal.getMemberId());
        req.setProvider("TOSS");
        PaymentIntentCreateResponse intent = paymentService.createIntent(req);
        Map<String, Object> data =Map.of(
                "clientKey",  tossClientKey,
                "orderId",    req.getOrderId(),
                "amount",     intent.getAmount(),
                "orderName",  intent.getOrderName(),
                "successUrl", successUrl,
                "failUrl",    failUrl
        );
        log.info("프론트엔드로 전달하는 데이터: {}", data);


        return ResponseEntity.ok(
                new ResponseDto<>(
                        200,
                        "세션 생성 성공",data)
        );
    }
}
