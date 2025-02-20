package com.ssginc.unnie.review.controller;

import com.ssginc.unnie.review.dto.ReceiptRequest;
import com.ssginc.unnie.review.dto.ReceiptResponse;
import com.ssginc.unnie.review.service.OCRService;
import com.ssginc.unnie.review.service.ReceiptService;
import com.ssginc.unnie.review.ReviewOCR.OCRParser;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OCRController {

    private final OCRService ocrService;
    private final ReceiptService receiptService;

    /**
     * OCR을 통해 영수증을 분석하고, DB에 저장하는 API
     */
    @PostMapping("/upload")
    public ResponseEntity<ReceiptResponse> uploadReceipt(@RequestParam("file") MultipartFile file) {
        // 파일 수신 확인 로그
        System.out.println("파일 업로드 요청 수신됨: " + file.getOriginalFilename());
        System.out.println("파일 크기: " + file.getSize());

        // OCR 처리 (JSON 직접 반환)
        JSONObject ocrJson = ocrService.processOCR(file);
        System.out.println("OCR JSON 응답: " + ocrJson.toString(2)); // ✅ JSON 응답 확인

        // OCR 데이터를 DTO로 변환
        ReceiptRequest receiptRequest = OCRParser.parse(ocrJson);

        // DB 저장
        ReceiptResponse savedReceipt = receiptService.saveReceipt(receiptRequest);

        return ResponseEntity.ok(savedReceipt);
    }
}
