package com.ssginc.unnie.common.util.validation;

import com.ssginc.unnie.common.exception.UnnieReportException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.like.vo.LikeTargetType;
import com.ssginc.unnie.report.dto.ReportRequest;
import com.ssginc.unnie.report.vo.ReportReason;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 신고 유효성 검증 클래스
 */
@Component
public class ReportValidator implements Validator<ReportRequest> {

    @Override
    public boolean validate(ReportRequest report) {

        if (report == null) {
            throw new UnnieReportException(ErrorCode.NULL_POINTER_ERROR);
        }

        if (report.getReportReason() == null) {
            throw new UnnieReportException(ErrorCode.REPORT_REASON_REQUIRED);
        }

        if (!isValidReasonType(report.getReportReason())) {
            throw new UnnieReportException(ErrorCode.REPORT_TYPE_INVALID);
        }

        if (report.getReportReasonDetailed().length() > 300) {
            throw new UnnieReportException(ErrorCode.REPORT_LENGTH_INVALID);
        }

        return false;
    }

    // 유효성 검증 메서드 추가
    private boolean isValidReasonType(String value) {
        return Arrays.stream(ReportReason.values())
                .map(ReportReason::name)
                .anyMatch(v -> v.equalsIgnoreCase(value));
    }
}
