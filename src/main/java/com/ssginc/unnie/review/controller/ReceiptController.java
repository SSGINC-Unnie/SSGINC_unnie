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
     * OCR 분석된 영수증을 DB에 저장하는 API
     */
    @PostMapping("/save")
    public ResponseEntity<ResponseDto<ReceiptResponse>> saveReceipt(@RequestBody ReceiptRequest receiptRequest) {
        log.info("영수증 저장 요청: {}", receiptRequest);

        try {
            // ✅ 유효성 검증
            if (!receiptValidator.validate(receiptRequest)) {
                log.error("유효하지 않은 영수증 데이터: {}", receiptRequest);
                return ResponseEntity.badRequest().body(new ResponseDto<>(400, "유효하지 않은 영수증 데이터", null));
            }

            // ✅ 영수증 저장
            ReceiptResponse savedReceipt = receiptService.saveReceipt(receiptRequest);
            log.info("영수증 저장 완료: {}", savedReceipt);

            // 성공 응답
            return ResponseEntity.ok(new ResponseDto<>(200, "영수증 저장 성공", savedReceipt));

        } catch (Exception e) {
            log.error("영수증 저장 중 오류 발생: {}", e.getMessage(), e);

            // 실패 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto<>(500, "영수증 저장 중 오류 발생", null));
        }
    }

    /**
     * 특정 영수증 조회 API
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<ReceiptResponse>> getReceipt(@PathVariable Long id) {
        log.info("영수증 조회 요청 (ID: {})", id);

        try {
            ReceiptResponse receipt = receiptService.getReceiptById(id);
            return ResponseEntity.ok(new ResponseDto<>(200, "영수증 조회 성공", receipt));

        } catch (IllegalArgumentException e) {
            log.error("영수증 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDto<>(404, "해당 영수증을 찾을 수 없습니다.", null));
        }
    }
}