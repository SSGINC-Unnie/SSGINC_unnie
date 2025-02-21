package com.ssginc.unnie.review.service;

import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public interface OCRService {
    /**
     * OCR을 통해 이미지에서 텍스트를 추출하는 메서드
     *
     * @param file 업로드된 영수증 이미지 파일
     * @return OCR 처리된 텍스트 결과
     */
    JSONObject processOCR(MultipartFile file);
}
