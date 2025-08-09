// PaymentController.java
package com.ssginc.unnie.payment.controller;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.payment.dto.*;
import com.ssginc.unnie.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j @RestController
@RequestMapping("/api/payments") @RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/intents")
    public ResponseEntity<ResponseDto<PaymentIntentCreateResponse>> createIntent(
            @RequestBody PaymentIntentCreateRequest req) {
        var res = paymentService.createIntent(req);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "결제 의도 생성 성공", res));
    }

    @PostMapping("/approve")
    public ResponseEntity<ResponseDto<Void>> approve(@RequestBody PaymentApproveRequest req) {
        paymentService.approve(req);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "결제 승인/예약 확정 완료", null));
    }
}
