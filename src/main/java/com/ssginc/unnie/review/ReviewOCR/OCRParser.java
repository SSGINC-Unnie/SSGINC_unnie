package com.ssginc.unnie.review.ReviewOCR;

import com.ssginc.unnie.common.util.validation.OCRValidator;
import com.ssginc.unnie.review.dto.ReceiptItemRequest;
import com.ssginc.unnie.review.dto.ReceiptRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ssginc.unnie.common.util.validation.OCRValidator.isNumeric;

@Slf4j
public class OCRParser {

    private static final OCRValidator validator = new OCRValidator();

    public static ReceiptRequest parse(JSONObject jsonObject) {
        try {
            log.info(jsonObject.toString(2));

            JSONArray images = jsonObject.optJSONArray("images");
            if (images == null || images.isEmpty()) {
                throw new RuntimeException("OCR API 응답에 'images' 키가 없습니다.");
            }

            JSONObject image = images.getJSONObject(0);
            JSONArray fields = image.optJSONArray("fields");
            if (fields == null || fields.isEmpty()) {
                throw new RuntimeException("OCR API 응답에 'fields' 키가 없습니다.");
            }

            // ✅ 텍스트 데이터 하나로 결합 (더 유연한 정규식 적용 가능)
            StringBuilder ocrText = new StringBuilder();
            for (int i = 0; i < fields.length(); i++) {
                ocrText.append(fields.getJSONObject(i).optString("inferText", "")).append(" ");
            }
            String fullText = ocrText.toString().replaceAll("\\s+", " ").trim();
            log.debug("OCR 텍스트 데이터: {}", fullText);

            // ✅ OCRValidator 를 사용하여 OCR 데이터 검증
            if (!validator.validate(fullText)) {
                throw new RuntimeException("OCR 데이터가 유효하지 않습니다.");
            }

            // ✅ OCRValidator 를 사용하여 데이터 추출
            String receiptShopName = OCRValidator.extractShopName(fullText);
            LocalDateTime receiptDate = OCRValidator.extractDateTime(fields);
            String businessNumber = OCRValidator.extractBusinessNumber(fullText);
            String approvalNumber = OCRValidator.extractApprovalNumber(fullText);
            int receiptAmount = OCRValidator.extractAmount(fullText);
            List<ReceiptItemRequest> items = extractItems(fields);


            return new ReceiptRequest(1, receiptDate, receiptAmount, businessNumber, approvalNumber, receiptShopName, items);

        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 오류: " + e.getMessage(), e);
        }
    }

    /**
     * 🔹 품목 리스트 추출
     */
    private static List<ReceiptItemRequest> extractItems(JSONArray fields) {
        List<ReceiptItemRequest> items = new ArrayList<>();
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
