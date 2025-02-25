package com.ssginc.unnie.review.service.serviceImpl;

import com.ssginc.unnie.review.service.OCRService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
@Slf4j
@Service
public class OCRServiceImpl implements OCRService {

    @Value("${naver.ocr.url}")
    private String OCR_URL;

    @Value("${naver.ocr.secret}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public JSONObject processOCR(MultipartFile file) {
        try {
            // ✅ MultipartFile을 Base64 인코딩된 String으로 변환
            String base64Image = convertFileToBase64(file);

            // ✅ JSON 요청 본문 구성
            JSONObject json = new JSONObject();
            json.put("version", "V2");
            json.put("requestId", UUID.randomUUID().toString());
            json.put("timestamp", System.currentTimeMillis());

            JSONObject image = new JSONObject();
            image.put("format", "jpg");
            image.put("name", file.getOriginalFilename());
            image.put("data", base64Image);  // 🔥 Base64 인코딩된 이미지 데이터 추가

            JSONArray images = new JSONArray();
            images.put(image);
            json.put("images", images);
            //{"images" : [json]}
            //{"images" : [{format:jpg, name:filename, data:dsklfjsdflsfj}]}
            //{version: V2, requestId: dkslfkdsfd, timestamp:20250115, images: [{~~~}]}            // ✅ HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-OCR-SECRET", secretKey);

            HttpEntity<String> requestEntity = new HttpEntity<>(json.toString(), headers);

            // 🚀 API 요청 로그 출력
            log.info("OCR API 요청 URL: {}", OCR_URL);
            log.info("OCR API 요청 본문: {}", json.toString(2));
            log.info("OCR API 요청 헤더: {}", headers);


            // ✅ OCR API 요청 보내기
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    OCR_URL, HttpMethod.POST, requestEntity, String.class
            );

            // 🚀 응답 상태 코드 출력
            log.info("OCR API 응답 코드: {}", responseEntity.getStatusCode());
            // 🚀 응답 본문 출력
            log.info("OCR API 원본 응답: {}", responseEntity.getBody());

            // ✅ JSON 변환
            return new JSONObject(responseEntity.getBody());

        } catch (Exception e) {
            throw new RuntimeException("OCR API 요청 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ MultipartFile을 Base64로 변환
     */
    private String convertFileToBase64(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        return Base64.encodeBase64String(fileBytes);
    }
}
