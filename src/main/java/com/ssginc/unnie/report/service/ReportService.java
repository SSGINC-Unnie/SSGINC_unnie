package com.ssginc.unnie.report.service;

import com.ssginc.unnie.report.dto.ReportRequest;

/**
 * 신고 기능 관련 인터페이스
 */
public interface ReportService {
    long createReport(ReportRequest report);

    long insertReport(ReportRequest report);

    void sendNotificationToAdmins(long reportId, int targetType);
}
