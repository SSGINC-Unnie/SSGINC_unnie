package com.ssginc.unnie.review.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.review.dto.ReceiptRequest;
import com.ssginc.unnie.review.dto.ReceiptResponse;
import com.ssginc.unnie.review.service.OCRService;
import com.ssginc.unnie.review.service.ReceiptService;
import com.ssginc.unnie.review.ReviewOCR.OCRParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
@Slf4j
public class OCRController {

    private final OCRService ocrService;
    private final ReceiptService receiptService;

    /**
     * OCR을 통해 영수증을 분석하고, DB에 저장하는 API
     */
    @PostMapping("/upload")
    public ResponseEntity<ResponseDto<ReceiptResponse>> uploadReceipt(@RequestParam("file") MultipartFile file) {
        log.info("파일 업로드 요청 수신됨: {}", file.getOriginalFilename());

        try {
            // OCR 처리
            JSONObject ocrJson = processOCR(file);
            log.info("OCR 분석 결과: {}", ocrJson.toString());

            // OCR 데이터 파싱
            ReceiptRequest receiptRequest = OCRParser.parse(ocrJson);
            log.info("파싱된 영수증 데이터: {}", receiptRequest);

            // 영수증 저장
            ReceiptResponse savedReceipt = saveReceipt(receiptRequest);
            log.info("영수증 저장 완료: {}", savedReceipt);

            // 성공 응답
            return ResponseEntity.ok(new ResponseDto<>(200, "파일 업로드 및 영수증 저장 성공", savedReceipt));

        } catch (Exception e) {
            log.error("파일 업로드 중 오류 발생: {}", e.getMessage(), e);

            // 실패 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto<>(500, "파일 업로드 중 오류 발생", null));
        }
    }

    /**
     * OCR 처리
     */
    private JSONObject processOCR(MultipartFile file) {
        return ocrService.processOCR(file);
    }

    /**
     * 응답 받은 JSON 영수증 Table 에 저장
     */
    private ReceiptResponse saveReceipt(ReceiptRequest receiptRequest) {
        return receiptService.saveReceipt(receiptRequest);
    }

}
