package com.ssginc.unnie.report.mapper;

import com.ssginc.unnie.admin.dto.report.AdminReportDeleteRequest;
import com.ssginc.unnie.admin.dto.report.AdminReportDetailResponse;
import com.ssginc.unnie.admin.dto.report.AdminReportRequest;
import com.ssginc.unnie.admin.dto.report.AdminReportsResponse;
import com.ssginc.unnie.report.dto.ReportRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 신고 기능 관련 Mapper
 */
@Mapper
public interface ReportMapper {

    Boolean isOwnContent(@Param("reportTargetType") int reportTargetType,
                         @Param("reportTargetId") long reportTargetId,
                         @Param("reportMemberId") long reportMemberId);


    Boolean isTargetExists(@Param("reportTargetType") int reportTargetType, @Param("reportTargetId") long reportTargetId);


    boolean isDuplicateReport(int reportTargetType,
                              long reportTargetId,
                              long reportMemberId);

    int insertReport(ReportRequest report);

    List<AdminReportsResponse> getAllReports(AdminReportRequest reportRequest);

    AdminReportDetailResponse getReportById(long reportId);

    boolean checkReportId(long reportId);

    int ignoreReport(long reportId);

    int softDeleteReportById(AdminReportDeleteRequest report);
}
