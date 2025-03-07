package com.ssginc.unnie.common.util.parser;

import com.ssginc.unnie.common.util.validation.OCRValidator;
import com.ssginc.unnie.review.dto.ReceiptItemRequest;
import com.ssginc.unnie.review.dto.ReceiptRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ssginc.unnie.common.util.validation.OCRValidator.isNumeric;

@Slf4j
@RequiredArgsConstructor
public class OCRParser {

    // OCRValidator 인스턴스 (정적 메서드 활용 때문에 DI 대신 static 사용)
    private static final OCRValidator validator = new OCRValidator();

    public static ReceiptRequest parse(JSONObject ocrResponse) {
        try {
            log.info("OCR 응답 JSON:\n{}", ocrResponse.toString(2));

            // images 배열 추출 및 검증
            JSONArray images = ocrResponse.optJSONArray("images");
            if (images == null || images.isEmpty()) {
                throw new RuntimeException("OCR API 응답에 'images' 키가 없습니다.");
            }

            // 첫 번째 이미지에서 fields 배열 추출 및 검증
            JSONObject image = images.getJSONObject(0);
            JSONArray fields = image.optJSONArray("fields");
            if (fields == null || fields.isEmpty()) {
                throw new RuntimeException("OCR API 응답에 'fields' 키가 없습니다.");
            }

            // 전체 OCR 텍스트 추출
            String fullText = extractFullText(fields);
            log.debug("추출된 OCR 텍스트: {}", fullText);

            // OCR 텍스트 유효성 검증
            if (!validator.validate(fullText)) {
                throw new RuntimeException("OCR 데이터가 유효하지 않습니다.");
            }

            // 각종 데이터 추출
            String receiptShopName = OCRValidator.extractShopName(fullText);
            LocalDateTime receiptDate = OCRValidator.extractDateTime(fields);
            String businessNumber = OCRValidator.extractBusinessNumber(fullText);
            String approvalNumber = OCRValidator.extractApprovalNumber(fullText);
            int receiptAmount = OCRValidator.extractAmount(fullText);
            List<ReceiptItemRequest> items = extractItems(fields);

            // ReceiptRequest 생성 (영수증 ID는 이후 DB에서 생성 혹은 별도 처리)
            return new ReceiptRequest(1L, 1, receiptDate, receiptAmount, businessNumber, approvalNumber, receiptShopName, items);

        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 오류: " + e.getMessage(), e);
        }
    }

    /**
     * fields 배열에서 모든 inferText 값을 하나의 문자열로 결합하여 반환
     */
    private static String extractFullText(JSONArray fields) {
        StringBuilder ocrText = new StringBuilder();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            ocrText.append(field.optString("inferText", "")).append(" ");
        }
        return ocrText.toString().replaceAll("\\s+", "").trim();
    }


    /**
     * 품목 리스트 추출 (원래 로직: fields 배열의 연속된 4개의 필드를 슬라이딩 윈도우 방식으로 추출)
     */
    private static List<ReceiptItemRequest> extractItems(JSONArray fields) {
        List<ReceiptItemRequest> items = new ArrayList<>();
        // 슬라이딩 윈도우 방식: 인접한 4개 필드를 하나의 품목 데이터로 판단
        for (int i = 0; i < fields.length() - 3; i++) {
            JSONObject field1 = fields.getJSONObject(i);
            JSONObject field2 = fields.getJSONObject(i + 1);
            JSONObject field3 = fields.getJSONObject(i + 2);
            JSONObject field4 = fields.getJSONObject(i + 3);

            String itemName = field1.optString("inferText", "").trim();
            String priceText = field2.optString("inferText", "").trim();
            String quantityText = field3.optString("inferText", "").trim();
            String totalText = field4.optString("inferText", "").trim();

            if (isNumeric(priceText) && isNumeric(quantityText) && isNumeric(totalText)) {
                try {
                    int price = Integer.parseInt(priceText.replaceAll(",", ""));
                    int quantity = Integer.parseInt(quantityText);
                    items.add(new ReceiptItemRequest(1, 1, itemName, price, quantity));
                } catch (NumberFormatException e) {
                    System.out.println("품목 데이터 변환 오류: " + itemName);
                }
            }
        }
        return items;
    }
}
