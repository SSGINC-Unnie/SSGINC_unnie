package com.ssginc.unnie.review.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.common.util.validation.Validator;
import com.ssginc.unnie.review.dto.ReceiptRequest;
import com.ssginc.unnie.review.dto.ReceiptResponse;
import com.ssginc.unnie.review.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
@Slf4j
public class ReceiptController {

    private final ReceiptService receiptService;
    private final Validator<ReceiptRequest> receiptValidator;

    /**
     * OCR 분석된 영수증을 DB에 저장하는
     */
    @PostMapping("/save")
    public ResponseEntity<ResponseDto<ReceiptResponse>> saveReceipt(@RequestBody ReceiptRequest receiptRequest) {
        log.info("영수증 저장 요청: {}", receiptRequest);

        // 검증 실패 시 서비스 또는 글로벌 예외 핸들러에서 처리하도록
        // 잘못된 데이터가 서비스 로직에 전달되지 않도록 초기 단계에서 바로 차단
        if (!receiptValidator.validate(receiptRequest)) {
            throw new IllegalArgumentException("유효하지 않은 영수증 데이터");
        }

        ReceiptResponse savedReceipt = receiptService.saveReceipt(receiptRequest);
        log.info("영수증 저장 완료: {}", savedReceipt);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto<>(HttpStatus.CREATED.value(), "영수증 저장 성공", savedReceipt));
    }

    /**
     * 특정 영수증 조회
     */
    @GetMapping("/{receiptId}")
    public ResponseEntity<ResponseDto<ReceiptResponse>> getReceipt(@PathVariable long receiptId) {
        log.info("영수증 조회 요청 (ID: {})", receiptId);
        ReceiptResponse receipt = receiptService.getReceiptById(receiptId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "영수증 조회 성공", receipt));
    }
}
