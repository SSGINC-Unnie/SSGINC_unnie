package com.ssginc.unnie.report.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieReportException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.Validator;
import com.ssginc.unnie.member.mapper.MemberMapper;
import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.mapper.NotificationMapper;
import com.ssginc.unnie.notification.vo.NotificationType;
import com.ssginc.unnie.report.dto.ReportRequest;
import com.ssginc.unnie.report.mapper.ReportMapper;
import com.ssginc.unnie.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 신고 관련 기능 구현 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    private final Validator<ReportRequest> reportValidator;
    private final MemberMapper memberMapper;
    private final NotificationMapper notificationMapper;

    /**
     * 신고 추가 메서드
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createReport(ReportRequest report) {
        // 입력 내용 유효성 검증
        reportValidator.validate(report);

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

        long reportId = insertReport(report);

        sendNotificationToAdmins(reportId, report.getReportTargetType());

        return reportId;
    }

    /**
     * 신고 저장 처리
     */
    @Override
    public long insertReport(ReportRequest report) {
        int res = reportMapper.insertReport(report);

        if (res == 0) {
            log.error("신고 저장 실패: targetId={}, reporterId={}", report.getReportTargetId(), report.getReportMemberId());
            throw new UnnieReportException(ErrorCode.REPORT_CREATE_FAILED);
        }

        return report.getReportId();
    }

    /**
     * 관리자들에게 새로운 신고 알림을 생성하는 메서드
     */
    @Override
    public void sendNotificationToAdmins(long reportId, int targetType) {
        // 1. 모든 관리자의 ID 조회
        List<Long> adminIds = memberMapper.findAllAdminMemberIds();
        if (adminIds.isEmpty()) {
            log.warn("알림을 받을 관리자가 시스템에 존재하지 않습니다.");
            return;
        }

        // 2. 알림 내용 생성
        String targetName = "";
        switch (targetType) {
            case 1: targetName = "게시글"; break;
            case 2: targetName = "댓글"; break;
            case 3: targetName = "리뷰"; break;
        }

        String content = String.format("새로운 %s 신고가 접수되었습니다. (신고 ID: %d)", targetName, reportId);
        String urn = "/admin/reports/" + reportId;

        // 3. 각 관리자에게 알림 생성
        for (Long adminId : adminIds) {
            NotificationMessage notification = new NotificationMessage();
            notification.setNotificationType(NotificationType.WARN);
            notification.setNotificationContents(content);
            notification.setNotificationUrn(urn);
            notification.setNotificationMemberId(adminId);

            notificationMapper.insertNotification(notification);
        }
        log.info("{}명의 관리자에게 신고 ID {} 에 대한 알림을 생성했습니다.", adminIds.size(), reportId);
    }
}