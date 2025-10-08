package com.ssginc.unnie.admin.dto.report;

import com.ssginc.unnie.report.vo.ReportReason;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 관리자 신고 상세 조회 전용 DTO
 */

@Getter
@Setter
public class AdminReportDetailResponse {
    private long reportId; // 신고 식별 번호
    private String reportTargetType; // 신고 컨텐츠 타입
    private long reportTargetId; // 신고 컨텐츠 식별 번호
    private String reportReason; // 신고 사유
    private String reportReasonDetailed; // 신고 상세 내용
    private LocalDateTime reportCreatedAt; // 신고일
    private LocalDateTime reportResolvedAt; // 신고 처리 일시
    private String reportStatus; // 처리 상태(미처리 / 처리 / 무시)
    private long reportMemberId; // 신고자 식별 번호
    private String reportedContent;
}
