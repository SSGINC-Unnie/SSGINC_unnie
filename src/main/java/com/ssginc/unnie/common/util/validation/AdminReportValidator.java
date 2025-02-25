package com.ssginc.unnie.common.util.validation;

import com.ssginc.unnie.admin.dto.report.AdminReportRequest;
import com.ssginc.unnie.common.exception.UnnieReportException;
import com.ssginc.unnie.common.util.ErrorCode;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class AdminReportValidator implements Validator<AdminReportRequest> {

    private static final LocalDate MIN_DATE = LocalDate.of(2025, 1, 1); // 최소 허용 날짜
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public boolean validate(AdminReportRequest report) {

        if (report == null){
            throw new UnnieReportException(ErrorCode.NULL_POINTER_ERROR);
        }

        if (report.getPage() < 1) {
            throw new UnnieReportException(ErrorCode.PAGE_OUT_OF_RANGE);
        }

        if (report.getReportTargetType() < 0 || report.getReportTargetType() > 3) {
            throw new UnnieReportException(ErrorCode.REPORT_TYPE_INVALID);
        }

        if (report.getReportStatus() != null) {
            int status = Integer.parseInt(report.getReportStatus());
            if (status < 0 || status > 3 ) {
            throw new UnnieReportException(ErrorCode.REPORT_INVALID_STATUS);
            }
        }

        if (!"ADMIN".equals(report.getMemberRole())) {
            throw new UnnieReportException(ErrorCode.FORBIDDEN);
        }

        LocalDate startDate = parseDate(report.getStartDate(), "startDate");
        LocalDate endDate = parseDate(report.getEndDate(), "endDate");
        LocalDate today = LocalDate.now();

        // startDate는 2025-01-01 이상이어야 함
        if (startDate.isBefore(MIN_DATE)) {
            throw new UnnieReportException(ErrorCode.REPORT_INVALID_DATE);
        }

        // endDate는 오늘 날짜를 초과할 수 없음
        if (endDate.isAfter(today)) {
            throw new UnnieReportException(ErrorCode.REPORT_INVALID_DATE);
        }

        // startDate가 endDate보다 이후일 경우 예외 발생
        if (startDate.isAfter(endDate)) {
            throw new UnnieReportException(ErrorCode.REPORT_INVALID_DATE);
        }

        return true;
    }

    /**
     * 날짜 문자열을 LocalDate로 변환하는 메서드 (예외 처리 포함)
     */
    private LocalDate parseDate(String dateStr, String fieldName) {
        if (dateStr == null || dateStr.isBlank()) {
            throw new UnnieReportException(ErrorCode.REPORT_INVALID_DATE);
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new UnnieReportException(ErrorCode.REPORT_INVALID_DATE);
        }
    }
}
