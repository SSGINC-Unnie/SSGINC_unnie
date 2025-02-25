package com.ssginc.unnie.admin.service;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.admin.dto.report.AdminReportDeleteRequest;
import com.ssginc.unnie.admin.dto.report.AdminReportDetailResponse;
import com.ssginc.unnie.admin.dto.report.AdminReportsResponse;

/**
 * 관리자 신고 관리 인터페이스
 */
public interface AdminReportService {
    PageInfo<AdminReportsResponse> getAllReports(int targetType, String status, String startDate, String endDate, int page, String role);

    AdminReportDetailResponse getReportById(long reportId, String role);

    void ignoreReport(long reportId, String role);

    void softDeleteReportById(AdminReportDeleteRequest reportId, String role);
}
