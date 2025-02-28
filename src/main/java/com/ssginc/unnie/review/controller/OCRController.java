package com.ssginc.unnie.review.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.common.util.parser.OCRParser;
import com.ssginc.unnie.review.dto.ReceiptRequest;
import com.ssginc.unnie.review.service.OCRService;
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

    /**
     * OCR을 통해 영수증을 분석하는 API (DB 저장 X)
     */
    @PostMapping("/upload")
    public ResponseEntity<ResponseDto<ReceiptRequest>> uploadReceipt(@RequestParam("file") MultipartFile file) {
        log.info("파일 업로드 요청: {}", file.getOriginalFilename());

        try {
            // OCR 처리
            JSONObject ocrJson = ocrService.processOCR(file);
            log.info("OCR 분석 결과: {}", ocrJson);

            // OCR 데이터 파싱
            ReceiptRequest receiptRequest = OCRParser.parse(ocrJson);
            log.info("파싱된 영수증 데이터: {}", receiptRequest);

            // 성공 응답 (DB 저장 X)
            return ResponseEntity.ok(new ResponseDto<>(200, "OCR 분석 성공", receiptRequest));

        } catch (Exception e) {
            log.error("OCR 분석 실패: {}", e.getMessage(), e);

            // 실패 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto<>(500, "OCR 분석 중 오류 발생", null));
        }
    }
}
