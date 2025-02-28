package com.ssginc.unnie.admin.service;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.admin.dto.report.AdminReportDeleteRequest;
import com.ssginc.unnie.admin.dto.report.AdminReportDetailResponse;
import com.ssginc.unnie.admin.dto.report.AdminReportsResponse;
import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.dto.NotificationResponse;

/**
 * 관리자 신고 관리 인터페이스
 */
public interface AdminReportService {
    PageInfo<AdminReportsResponse> getAllReports(int targetType, String status, String startDate, String endDate, int page, String role);

    AdminReportDetailResponse getReportById(long reportId, String role);

    void ignoreReport(long reportId, String role);

    void softDeleteReportById(AdminReportDeleteRequest reportId, String role);

    NotificationResponse getReportTargetMemberInfoByTargetInfo(AdminReportDeleteRequest report);

    NotificationMessage createNotificationMsg(AdminReportDeleteRequest report, NotificationResponse response);
}
