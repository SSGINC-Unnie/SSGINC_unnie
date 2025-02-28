package com.ssginc.unnie.admin.mapper;

import com.ssginc.unnie.admin.dto.report.AdminReportDeleteRequest;
import com.ssginc.unnie.admin.dto.report.AdminReportDetailResponse;
import com.ssginc.unnie.admin.dto.report.AdminReportRequest;
import com.ssginc.unnie.admin.dto.report.AdminReportsResponse;
import com.ssginc.unnie.notification.dto.NotificationResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminReportMapper {

    List<AdminReportsResponse> getAllReports(AdminReportRequest reportRequest);

    AdminReportDetailResponse getReportById(long reportId);

    boolean checkReportId(long reportId);

    int ignoreReport(long reportId);

    int softDeleteReportById(AdminReportDeleteRequest report);

    NotificationResponse getReportTargetMemberInfoByTargetInfo(AdminReportDeleteRequest report);
}
