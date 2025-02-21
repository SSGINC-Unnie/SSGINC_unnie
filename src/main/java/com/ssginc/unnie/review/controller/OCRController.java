package com.ssginc.unnie.review.controller;

import com.ssginc.unnie.review.dto.ReceiptRequest;
import com.ssginc.unnie.review.dto.ReceiptResponse;
import com.ssginc.unnie.review.service.OCRService;
import com.ssginc.unnie.review.service.ReceiptService;
import com.ssginc.unnie.review.ReviewOCR.OCRParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
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
    public ResponseEntity<ReceiptResponse> uploadReceipt(@RequestParam("file") MultipartFile file) {
        log.info("📤 파일 업로드 요청 수신됨: {}", file.getOriginalFilename());

        JSONObject ocrJson = processOCR(file);
        ReceiptRequest receiptRequest = OCRParser.parse(ocrJson);
        ReceiptResponse savedReceipt = saveReceipt(receiptRequest);

        return ResponseEntity.ok(savedReceipt);
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
