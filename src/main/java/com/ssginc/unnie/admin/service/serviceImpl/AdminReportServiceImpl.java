package com.ssginc.unnie.admin.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.admin.dto.report.*;
import com.ssginc.unnie.admin.mapper.AdminReportMapper;
import com.ssginc.unnie.admin.service.AdminReportService;
import com.ssginc.unnie.common.exception.UnnieReportException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.Validator;
import com.ssginc.unnie.notification.dto.NotificationResponse;
import com.ssginc.unnie.notification.mapper.NotificationMapper;
import com.ssginc.unnie.notification.vo.Notification;
import com.ssginc.unnie.report.vo.ReportReason;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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

    private final AdminReportMapper reportMapper;
    private final Validator<AdminReportRequest> validator;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationMapper notificationMapper;

    /**
     * 신고 목록 조회
     */
    @Transactional(readOnly = true)
    @Override
    public PageInfo<AdminReportsResponse> getAllReports(Integer targetType, String status, String startDate, String endDate, int page, String role) {

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
        AdminReportDetailResponse responseDto = reportMapper.getReportById(reportId);
        if (responseDto == null) {
            throw new UnnieReportException(ErrorCode.ADMIN_REPORT_NOT_FOUND);
        }

        String reportedContent = null;
        long targetId = responseDto.getReportTargetId();

                reportedContent = reportMapper.getBoardContentById(targetId);

        responseDto.setReportedContent(reportedContent);

        if (responseDto.getReportReason() != null) {
            responseDto.setReportReason(ReportReason.getDescriptionFromName(responseDto.getReportReason()));
        }

        return responseDto;
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
        reportMapper.updateReportStatus(report.getReportId(), 1);

        if (res == 0) {
            throw new UnnieReportException(ErrorCode.REPORTED_CONTENT_DELETE_FAILED);
        }

        NotificationResponse notificationResponse = getReportTargetMemberInfoByTargetInfo(report);

        eventPublisher.publishEvent(
                ReportCreatedEvent.builder()
                        .receiverId(notificationResponse.getReceiverId())
                        .title(notificationResponse.getTargetTitle())
                        .build()
        );

    }

    /**
     * 신고 대상 식별 번호로 신고당한 유저 정보 조회
     */
    @Override
    public NotificationResponse getReportTargetMemberInfoByTargetInfo(AdminReportDeleteRequest report) {
        return reportMapper.getReportTargetMemberInfoByTargetInfo(report);
    }




    @Override
    @Transactional(readOnly = true)
    public PageInfo<Notification> getAdminNotifications(long adminId, int page) {
        PageHelper.startPage(page, 15);

        List<Notification> notifications = notificationMapper.findAllByMemberId(adminId);

        return new PageInfo<>(notifications);
    }


}
