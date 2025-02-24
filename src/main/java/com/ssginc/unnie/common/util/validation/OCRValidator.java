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

    // ✅ 가게 이름 추출 (상호: 또는 대괄호 [] 내부에서 추출)
    private static final String SHOP_NAME_REGEX = "(?:상\\s*호[:\\s]*([가-힣A-Za-z0-9\\s-]+)|\\[(.*?)\\])";

    // ✅ 사업자번호 추출 (하이픈 유무 포함, "사업자 번호" 형태도 가능하도록 개선)
    private static final String BUSINESS_NUMBER_REGEX = "(?:사업자\\s*번호\\s*[:]?\\s*)?(\\d{3}-?\\d{2}-?\\d{5}|\\d{10})";

    // ✅ 승인번호 추출 ("승인번호"와 "승인 번호" 모두 가능하도록 개선)
    private static final String APPROVAL_NUMBER_REGEX = "승인\\s*번호\\s*[:]?\\s*(\\d{6,})";

    // ✅ 결제 금액 추출 (숫자 + "원" 포함)
    private static final String AMOUNT_REGEX = "결제금액\\s*[^\\n]*?([\\d,]+)\\s*?(원)?";

    //✅ 결제 일시 추출
    private static final String DATE_REGEX = "(\\d{4}[-/.]\\d{1,2}[-/.]\\d{1,2})";
    private static final String TIME_HH_REGEX = "(\\d{1,2}):"; // HH: 형태
    private static final String TIME_MMSS_REGEX = "(\\d{1,2}:\\d{1,2})"; // MM:SS 형태


    /**
     * 🔹 Validator 인터페이스 구현: OCR 데이터의 유효성 검사
     */
    @Override
    public boolean validate(Object text) {
        if (text == null ) {
            log.warn("검증 실패: OCR 텍스트가 비어 있음");
            return false;
        }
        return true; // 텍스트가 존재하면 유효한 것으로 간주
    }

    /**
     * 🔹 정규식을 사용하여 특정 패턴 추출
     */
    public static String extractPattern(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) { //패턴에 맞는 문자열이 있으면
            //[null, "test"]
            //group(0)이면 [
            //group(1)이면 [null,
            //group(2)이면 [null, "test"
            return matcher.group(1) != null ? matcher.group(1).trim() : matcher.group(2).trim();
        }
        log.warn("정규식 '{}'에 해당하는 데이터를 찾을 수 없음", regex);
        return "데이터 없음";
    }

    /**
     * 🔹 가게 이름 추출
     */
    public static String extractShopName(String text) {
        return extractPattern(text, SHOP_NAME_REGEX);
    }

    /**
     * 🔹 사업자번호 추출
     */
    public static String extractBusinessNumber(String text) {
        return extractPattern(text, BUSINESS_NUMBER_REGEX);
    }

    /**
     * 🔹 승인번호 추출
     */
    public static String extractApprovalNumber(String text) {
        return extractPattern(text, APPROVAL_NUMBER_REGEX);
    }

    /**
     * 🔹 결제 금액 추출
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
     * 🔹 날짜 추출
     */
    public static LocalDateTime extractDateTime(JSONArray fields) {
        String datePart = null;
        String hour = "00", minute = "00", second = "00"; // 기본값

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String text = field.optString("inferText", "").trim();
            //optString(if문 포함, 조건체크할 수 있는 함수)
            //json인 field에 key가 inferText이면 기본값은 ""처리해주고, 없으면 공백을 잘라서
            //text에 넣어라.!

            // ✅ 날짜 찾기
            if (text.matches(DATE_REGEX)) {
                datePart = text;
            }

            // ✅ 시간 HH: 찾기 (예: "13:")
            else if (text.matches(TIME_HH_REGEX)) {
                String potentialHour = text.replace(":", ""); // "34:" → "34"
                if (isValidHour(potentialHour)) {
                    hour = potentialHour;
                }
                // 다음 항목에서 "MM:SS" 찾기
                if (i + 1 < fields.length()) {
                    String nextText = fields.getJSONObject(i + 1).optString("inferText", "").trim();
                    if (nextText.matches(TIME_MMSS_REGEX)) {
                        String[] timeParts = nextText.split(":");
                        if (isValidMinute(timeParts[0])) {
                            minute = timeParts[0];
                        }
                        if (timeParts.length > 1 && isValidSecond(timeParts[1])) {
                            second = timeParts[1];
                        }
                    }
                }
            }
        }

        if (datePart != null) {
            // ✅ 최종 값 검증 및 ISO-8601 변환
            //왜 ISO-8601로 변환했는가? 쟤가 뭐길래??
            String dateTimeString = datePart + "T" + hour + ":" + minute + ":" + second;
            return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        return LocalDateTime.now(); // 기본값 현재 시간 반환
    }

    // ✅ 시간 값이 0~23 사이인지 확인
    private static boolean isValidHour(String hour) {
        try {
            int h = Integer.parseInt(hour);
            return h >= 0 && h <= 23;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ✅ 분 값이 0~59 사이인지 확인
    private static boolean isValidMinute(String minute) {
        try {
            int m = Integer.parseInt(minute);
            return m >= 0 && m <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ✅ 초 값이 0~59 사이인지 확인
    private static boolean isValidSecond(String second) {
        try {
            int s = Integer.parseInt(second);
            return s >= 0 && s <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 🔹 문자열이 숫자인지 확인
     */
    public static boolean isNumeric(String str) {
        return str.matches("\\d+(,\\d{3})*");  // 쉼표 포함된 숫자 체크
    }
}
