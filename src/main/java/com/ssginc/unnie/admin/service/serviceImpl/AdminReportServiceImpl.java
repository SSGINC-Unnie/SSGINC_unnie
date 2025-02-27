package com.ssginc.unnie.admin.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.admin.dto.report.AdminReportDeleteRequest;
import com.ssginc.unnie.admin.dto.report.AdminReportDetailResponse;
import com.ssginc.unnie.admin.dto.report.AdminReportRequest;
import com.ssginc.unnie.admin.dto.report.AdminReportsResponse;
import com.ssginc.unnie.admin.service.AdminReportService;
import com.ssginc.unnie.common.exception.UnnieReportException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.Validator;
import com.ssginc.unnie.report.mapper.ReportMapper;
import com.ssginc.unnie.report.vo.ReportReason;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관리자 신고 관리 인터페이스 구현 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private final ReportMapper reportMapper;

    private final Validator<AdminReportRequest> validator;

    /**
     * 신고 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public PageInfo<AdminReportsResponse> getAllReports(int targetType, String status, String startDate, String endDate, int page, String role) {

        AdminReportRequest reportRequest = AdminReportRequest.builder()
                .reportTargetType(targetType)
                .reportStatus(status)
                .startDate(startDate)
                .endDate(endDate)
                .memberRole(role)
                .reportStatus(status)
                .page(page)
                .build();

        // 유효성 검증
        validator.validate(reportRequest);

        reportRequest.setEndDate(endDate + " 23:59:59"); // between 시 endDate 00:00:00 까지로 제한되므로

        PageHelper.startPage(page, 10);

        List<AdminReportsResponse> res = reportMapper.getAllReports(reportRequest);

        for (AdminReportsResponse r : res) {
            r.setReportReason(ReportReason.getDescriptionFromName(r.getReportReason()));
        }

        return new PageInfo<>(res);
    }


    /**
     * 신고 상세 조회
     */
    @Override
    @Transactional(readOnly = true)
    public AdminReportDetailResponse getReportById(long reportId, String role) {

        if (role == null || !role.equals("ADMIN")) {
            throw new UnnieReportException(ErrorCode.FORBIDDEN);
        }

        if (reportId <= 0) {
            throw new UnnieReportException(ErrorCode.REPORT_NOT_FOUND);
        }

        if (!reportMapper.checkReportId(reportId)){
            throw new UnnieReportException(ErrorCode.ADMIN_REPORT_NOT_FOUND);
        }

        AdminReportDetailResponse res = reportMapper.getReportById(reportId);

        if (res != null) {
            res.setReportReason(ReportReason.getDescriptionFromName(res.getReportReason()));
        }

        return res;
    }

    /**
     * 신고 무시
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ignoreReport(long reportId, String role) {

        if (role == null || !role.equals("ADMIN")) {
            throw new UnnieReportException(ErrorCode.FORBIDDEN);
        }

        if (reportId <= 0) {
            throw new UnnieReportException(ErrorCode.REPORT_NOT_FOUND);
        }

        if (!reportMapper.checkReportId(reportId)){
            throw new UnnieReportException(ErrorCode.ADMIN_REPORT_NOT_FOUND);
        }

        int res = reportMapper.ignoreReport(reportId);

        if (res == 0) {
            throw new UnnieReportException(ErrorCode.REPORT_UPDATE_FAILED);
        }
    }

    /**
     * 신고된 컨텐츠 삭제
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void softDeleteReportById(AdminReportDeleteRequest report, String role) {

        if (role == null || !role.equals("ADMIN")) {
            throw new UnnieReportException(ErrorCode.FORBIDDEN);
        }

        if (report.getReportId() <= 0) {
            throw new UnnieReportException(ErrorCode.REPORT_NOT_FOUND);
        }

        long reportId = report.getReportId();

        if (!reportMapper.checkReportId(reportId)){
            throw new UnnieReportException(ErrorCode.ADMIN_REPORT_NOT_FOUND);
        }

        int res = reportMapper.softDeleteReportById(report);

        if (res == 0) {
            throw new UnnieReportException(ErrorCode.REPORTED_CONTENT_DELETE_FAILED);
        }
    }


}
