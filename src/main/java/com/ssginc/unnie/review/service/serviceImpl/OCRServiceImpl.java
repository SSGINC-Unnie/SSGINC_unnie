package com.ssginc.unnie.review.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieReviewException;
import com.ssginc.unnie.common.util.ErrorCode;
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
            // MultipartFile을 Base64 인코딩된 String으로 변환
            String base64Image = convertFileToBase64(file);

            // JSON 요청 본문 구성
            JSONObject json = new JSONObject();
            json.put("version", "V2");
            json.put("requestId", UUID.randomUUID().toString());
            json.put("timestamp", System.currentTimeMillis());

            JSONObject image = new JSONObject();
            image.put("format", "jpg");
            image.put("name", file.getOriginalFilename());
            image.put("data", base64Image);  // Base64 인코딩된 이미지 데이터 추가

            JSONArray images = new JSONArray();
            images.put(image);
            json.put("images", images);

            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-OCR-SECRET", secretKey);

            HttpEntity<String> requestEntity = new HttpEntity<>(json.toString(), headers);

            // API 요청 로그 출력
            log.info("OCR API 요청 URL: {}", OCR_URL);
            log.info("OCR API 요청 본문: {}", json.toString(2));
            log.info("OCR API 요청 헤더: {}", headers);

            // OCR API 요청 보내기
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    OCR_URL, HttpMethod.POST, requestEntity, String.class
            );

            log.info("OCR API 응답 코드: {}", responseEntity.getStatusCode());
            log.info("OCR API 원본 응답: {}", responseEntity.getBody());

            return new JSONObject(responseEntity.getBody());

        } catch (IOException e) {
            log.error("파일 변환 중 오류 발생: {}", e.getMessage(), e);
            // 파일 변환 실패에 대한 에러코드가 필요하면 새로 생성할 수 있습니다.
            throw new UnnieReviewException(ErrorCode.OCR_PROCESSING_FAILED, e);
        } catch (Exception e) {
            log.error("OCR API 요청 중 오류 발생: {}", e.getMessage(), e);
            throw new UnnieReviewException(ErrorCode.OCR_PROCESSING_FAILED, e);
        }
    }

    /**
     * MultipartFile을 Base64로 변환하는 메서드
     */
    private String convertFileToBase64(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        return Base64.encodeBase64String(fileBytes);
    }
}
