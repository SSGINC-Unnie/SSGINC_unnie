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
public class OCRValidator implements Validator<Object> {

    // 가게 이름 추출 (상호: 또는 대괄호 [] 내부에서 추출)
    private static final String SHOP_NAME_REGEX = "(?:상\\s*호[:\\s]*([가-힣A-Za-z0-9\\s-]+)|\\[(.*?)\\])";

    // 사업자번호 추출 (하이픈 유무 포함, "사업자 번호" 형태도 가능하도록 개선)
    private static final String BUSINESS_NUMBER_REGEX = "(?:사업자\\s*번호\\s*[:]?\\s*)?(\\d{3}-?\\d{2}-?\\d{5}|\\d{10})";

    // 승인번호 추출 ("승인번호"와 "승인 번호" 모두 가능하도록 개선)
    private static final String APPROVAL_NUMBER_REGEX = "승인\\s*번호\\s*[:]?\\s*(\\d{6,})";

    // 결제 금액 추출 (숫자 + "원" 포함)
    private static final String AMOUNT_REGEX = "결제금액\\s*[^\\n]*?([\\d,]+)\\s*?(원)?";

    //결제 일시 추출
    private static final String DATE_REGEX = "(\\d{4}[-/.]\\d{1,2}[-/.]\\d{1,2})";
    private static final String TIME_HH_REGEX = "(\\d{1,2}):"; // HH: 형태
    private static final String TIME_MMSS_REGEX = "(\\d{1,2}:\\d{1,2})"; // MM:SS 형태

    /**
     * Validator 인터페이스 구현: OCR 데이터의 유효성 검사
     */
    @Override
    public boolean validate(Object text) {
        if (text == null) {
            log.warn("검증 실패: OCR 텍스트가 비어 있음");
            return false;
        }
        return true; // 텍스트가 존재하면 유효한 것으로 간주
    }

    /**
     * 정규식을 사용하여 특정 패턴 추출
     */
    public static String extractPattern(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) { // 패턴에 맞는 문자열이 있으면
            return matcher.group(1) != null ? matcher.group(1).trim() : matcher.group(2).trim();
        }
        log.warn("정규식 '{}'에 해당하는 데이터를 찾을 수 없음", regex);
        return "데이터 없음";
    }

    /**
     * 가게 이름 추출
     */
    public static String extractShopName(String text) {
        return extractPattern(text, SHOP_NAME_REGEX);
    }

    /**
     * 사업자번호 추출
     */
    public static String extractBusinessNumber(String text) {
        return extractPattern(text, BUSINESS_NUMBER_REGEX);
    }

    /**
     * 승인번호 추출
     */
    public static String extractApprovalNumber(String text) {
        return extractPattern(text, APPROVAL_NUMBER_REGEX);
    }

    /**
     * 결제 금액 추출
     */
    public static int extractAmount(String text) {
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
     * 날짜 추출
     */
    public static LocalDateTime extractDateTime(JSONArray fields) {
        String datePart = null;
        String hour = "00", minute = "00", second = "00"; // 기본값

        // 날짜 패턴은 그대로 사용 (예: "2025-02-28")
        Pattern datePattern = Pattern.compile(DATE_REGEX);
        // 시간 패턴: 공백 허용 - "HH:mm" 또는 "HH:mm:ss"
        Pattern timePattern = Pattern.compile("(\\d{1,2})\\s*:\\s*(\\d{1,2})(?:\\s*:\\s*(\\d{1,2}))?");

        // 모든 필드의 텍스트를 하나의 문자열로 결합
        StringBuilder aggregatedText = new StringBuilder();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String text = field.optString("inferText", "").trim();
            if (!text.isEmpty()) {
                aggregatedText.append(text).append(" ");
            }
        }
        String allText = aggregatedText.toString();

        // 날짜 추출 (날짜는 정상적으로 추출된다고 가정)
        Matcher dateMatcher = datePattern.matcher(allText);
        if (dateMatcher.find()) {
            datePart = dateMatcher.group(1);
            // 구분자가 "/" 또는 "."인 경우 "-"로 변환
            datePart = datePart.replace("/", "-").replace(".", "-");
        }

        // 개선된 시간 추출: 공백을 허용하여 시간 정보를 추출합니다.
        Matcher timeMatcher = timePattern.matcher(allText);
        if (timeMatcher.find()) {
            String extractedHour = timeMatcher.group(1);
            String extractedMinute = timeMatcher.group(2);
            String extractedSecond = timeMatcher.group(3); // 선택적 그룹

            if (isValidHour(extractedHour)) {
                hour = extractedHour;
            }
            if (isValidMinute(extractedMinute)) {
                minute = extractedMinute;
            }
            if (extractedSecond != null && isValidSecond(extractedSecond)) {
                second = extractedSecond;
            }
        }

        if (datePart != null) {
            String dateTimeString = datePart + "T" + hour + ":" + minute + ":" + second;
            try {
                return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                log.warn("날짜 변환 실패: {}", dateTimeString);
            }
        }
        return LocalDateTime.now(); // 기본값: 변환 실패 시 현재 시간 반환
    }




    // 시간 값이 0~23 사이인지 확인
    private static boolean isValidHour(String hour) {
        try {
            int h = Integer.parseInt(hour);
            return h >= 0 && h <= 23;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 분 값이 0~59 사이인지 확인
    private static boolean isValidMinute(String minute) {
        try {
            int m = Integer.parseInt(minute);
            return m >= 0 && m <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 초 값이 0~59 사이인지 확인
    private static boolean isValidSecond(String second) {
        try {
            int s = Integer.parseInt(second);
            return s >= 0 && s <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 문자열이 숫자인지 확인
     */
    public static boolean isNumeric(String str) {
        return str.matches("\\d+(,\\d{3})*");  // 쉼표 포함된 숫자 체크
    }
}
