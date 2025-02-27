package com.ssginc.unnie.common.util.validation;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class OCRValidator implements Validator<String> {

    // 정규식 패턴
    private static final String SHOP_NAME_REGEX = "(?:상\\s*호[:\\s]*([가-힣A-Za-z0-9\\s-]+)|\\[(.*?)\\])";
    private static final String BUSINESS_NUMBER_REGEX = "(?:사업자\\s*번호\\s*[:]?\\s*)?(\\d{3}-?\\d{2}-?\\d{5}|\\d{10})";
    private static final String APPROVAL_NUMBER_REGEX = "승인\\s*번호\\s*[:]?\\s*(\\d{6,})";
    private static final String AMOUNT_REGEX = "결제금액\\s*[^\\n]*?([\\d,]+)\\s*?(원)?";
    private static final String DATE_REGEX = "(\\d{4}[-/.]\\d{1,2}[-/.]\\d{1,2})";
    private static final String TIME_HH_REGEX = "(\\d{1,2}):"; // HH:
    private static final String TIME_MMSS_REGEX = "(\\d{1,2}:\\d{1,2})"; // MM:SS

    /**
     *  OCR 데이터가 존재하는지 검증
     */
    @Override
    public boolean validate(String ocrText) {
        if (ocrText == null || ocrText.trim().isEmpty()) {
            log.warn("OCR 데이터가 비어 있음");
            return false;
        }
        return true;
    }

    /**
     *  특정 패턴을 정규식을 사용해 추출
     */
    private static String extractPattern(String text, String regex) {
        if (text == null || text.trim().isEmpty()) return "데이터 없음";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1) != null ? matcher.group(1).trim() : matcher.group(2).trim();
        }
        log.warn("정규식 '{}'에 해당하는 데이터를 찾을 수 없음", regex);
        return "데이터 없음";
    }

    /**
     *  OCR 에서 상호명 추출
     */
    public static String extractShopName(String text) {
        return extractPattern(text, SHOP_NAME_REGEX);
    }

    /**
     *  OCR 에서 사업자번호 추출
     */
    public static String extractBusinessNumber(String text) {
        return extractPattern(text, BUSINESS_NUMBER_REGEX);
    }

    /**
     *  OCR 에서 승인번호 추출
     */
    public static String extractApprovalNumber(String text) {
        return extractPattern(text, APPROVAL_NUMBER_REGEX);
    }

    /**
     *  OCR 에서 결제 금액 추출
     */
    public static int extractAmount(String text) {
        if (text == null || text.trim().isEmpty()) return 0;

        Pattern amountPattern = Pattern.compile(AMOUNT_REGEX);
        Matcher matcher = amountPattern.matcher(text);

        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1).replaceAll(",", ""));
            } catch (NumberFormatException e) {
                log.warn("금액 변환 오류: {}", matcher.group(1));
            }
        }
        log.warn("결제 금액을 찾을 수 없음 → 기본값 0 사용");
        return 0;
    }

    /**
     *  OCR 에서 날짜 및 시간을 추출하여 LocalDateTime 으로 변환
     */
    public static LocalDateTime extractDateTime(JSONArray fields) {
        String datePart = null;
        String hour = "00", minute = "00", second = "00";

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String text = field.optString("inferText", "").trim();

            // 날짜 찾기
            if (text.matches(DATE_REGEX)) {
                datePart = text;
                continue;
            }

            // 시간 찾기 (HH: 형태)
            if (text.matches(TIME_HH_REGEX)) {
                String potentialHour = text.replace(":", "");
                if (isValidNumber(potentialHour, 0, 23)) {
                    hour = potentialHour;
                }

                // 다음 항목에서 MM:SS 찾기
                if (i + 1 < fields.length()) {
                    String nextText = fields.getJSONObject(i + 1).optString("inferText", "").trim();
                    if (nextText.matches(TIME_MMSS_REGEX)) {
                        String[] timeParts = nextText.split(":");
                        if (isValidNumber(timeParts[0], 0, 59)) {
                            minute = timeParts[0];
                        }
                        if (timeParts.length > 1 && isValidNumber(timeParts[1], 0, 59)) {
                            second = timeParts[1];
                        }
                    }
                }
            }
        }

        if (datePart != null) {
            String dateTimeString = datePart + "T" + hour + ":" + minute + ":" + second;
            try {
                return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                log.warn("날짜 변환 실패, 현재 시간 반환: {}", dateTimeString);
            }
        }

        return LocalDateTime.now();
    }

    /**
     *  숫자가 특정 범위 내에 있는지 확인
     */
    private static boolean isValidNumber(String value, int min, int max) {
        try {
            int num = Integer.parseInt(value);
            return num >= min && num <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     *  문자열이 숫자인지 확인
     */
    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+(,\\d{3})*");
    }
}
