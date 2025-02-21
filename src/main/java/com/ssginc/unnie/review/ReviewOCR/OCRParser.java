package com.ssginc.unnie.review.ReviewOCR;

import com.ssginc.unnie.review.dto.ReceiptItemRequest;
import com.ssginc.unnie.review.dto.ReceiptRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class OCRParser {

    public static ReceiptRequest parse(JSONObject jsonObject) {
        try {
            // 🔹 JSON 응답 log
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

            // ✅ OCR에서 추출한 데이터 저장 변수
            String shopName = "알 수 없음";
            String businessNumber = "";
            String approvalNumber = "";
            LocalDateTime receiptDate = LocalDateTime.now();
            int receiptAmount = 0;
            List<ReceiptItemRequest> items = new ArrayList<>();

            // ✅ 텍스트 데이터 하나로 결합 (더 유연한 정규식 적용 가능)
            StringBuilder ocrText = new StringBuilder();
            for (int i = 0; i < fields.length(); i++) {
                ocrText.append(fields.getJSONObject(i).optString("inferText", "")).append(" ");
            }
            String fullText = ocrText.toString().trim();

            // ✅ 가게 이름 추출 (상호: 뒤에 오는 첫 단어)
            shopName = extractPattern(fullText, "상호:\\s*([가-힣A-Za-z0-9\\s-]+)");

            // ✅ 사업자번호 추출
            businessNumber = extractPattern(fullText, "사업자번호\\s*[:\\s]*(\\d{3}-?\\d{2}-?\\d{5})");

            // ✅ 승인번호 추출
            approvalNumber = extractPattern(fullText, "승인번호\\s*[:\\s]*(\\d{6,})");

            // ✅ 결제 금액 추출
            receiptAmount = extractAmount(fullText);

            // ✅ 품목 리스트 추출
            items = extractItems(fields);

            // ✅ DTO 생성 후 반환
            return new ReceiptRequest(receiptDate, receiptAmount, businessNumber, approvalNumber, 1, 1, shopName, items);

        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 오류: " + e.getMessage(), e);
        }
    }

    /**
     * 🔹 정규식을 사용하여 특정 패턴 추출
     */
    private static String extractPattern(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.find() ? matcher.group(1).trim() : "";
        }
        log.warn("⚠️ 정규식 '{}'에 해당하는 데이터를 찾을 수 없음", regex);
        return "데이터 없음";
    }


    /**
     * 🔹 결제 금액 추출
     */
    private static int extractAmount(String text) {
        Pattern amountPattern = Pattern.compile("결제금액[^\n]*?([\\d,]+)\\s*원");
        Matcher matcher = amountPattern.matcher(text);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1).replaceAll(",", ""));
            } catch (NumberFormatException e) {
                System.out.println("⚠️ 금액 변환 오류: " + matcher.group(1));
            }
        }
        System.out.println("⚠️ 결제 금액을 찾을 수 없음 → 기본값 0 사용");
        return 0;
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
                    items.add(new ReceiptItemRequest(0, 0, itemName, price, quantity));
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ 품목 데이터 변환 오류: " + itemName);
                }
            }
        }
        return items;
    }

    /**
     * 🔹 문자열이 숫자인지 확인
     */
    private static boolean isNumeric(String str) {
        return str.matches("\\d+(,\\d{3})*");  // 쉼표 포함된 숫자 체크
    }
}
