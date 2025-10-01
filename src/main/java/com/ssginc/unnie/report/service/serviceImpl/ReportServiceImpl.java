package com.ssginc.unnie.report.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieReportException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.Validator;
import com.ssginc.unnie.report.dto.ReportRequest;
import com.ssginc.unnie.report.mapper.ReportMapper;
import com.ssginc.unnie.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 신고 관련 기능 구현 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    private final Validator<ReportRequest> reportValidator;

    /**
     * 신고 추가 메서드
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createReport(ReportRequest report) {
        // 입력 내용 유효성 검증
        reportValidator.validate(report);

        // 신고 대상 존재 여부 확인
        if (!reportMapper.isTargetExists(report.getReportTargetType(), report.getReportTargetId())) {
            log.error("신고 대상이 존재하지 않음: targetType={}, targetId={}", report.getReportTargetType(), report.getReportTargetId());
            throw new UnnieReportException(ErrorCode.REPORT_NOT_FOUND);
        }



        // 본인 컨텐츠 여부 검증
        if (reportMapper.isOwnContent(report.getReportTargetType(), report.getReportTargetId(), report.getReportMemberId())) {
            log.error("본인 콘텐츠 신고 시도: reporterId={}, targetId={}", report.getReportMemberId(), report.getReportTargetId());
            throw new UnnieReportException(ErrorCode.REPORT_SELF_FORBIDDEN);
        }

        // 중복 신고 여부 확인
        if (reportMapper.isDuplicateReport(report.getReportTargetType(), report.getReportTargetId(), report.getReportMemberId())) {
            log.error("중복 신고 시도: reporterId={}, targetId={}", report.getReportMemberId(), report.getReportTargetId());
            throw new UnnieReportException(ErrorCode.REPORT_DUPLICATE);
        }

        return insertReport(report);
    }

    /**
     * 신고 저장 처리
     */
    private long insertReport(ReportRequest report) {
        int res = reportMapper.insertReport(report);

        if (res == 0) {
            log.error("신고 저장 실패: targetId={}, reporterId={}", report.getReportTargetId(), report.getReportMemberId());
            throw new UnnieReportException(ErrorCode.REPORT_CREATE_FAILED);
        }

        return report.getReportId();
    }
}
