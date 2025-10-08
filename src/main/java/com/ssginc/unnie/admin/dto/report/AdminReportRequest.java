package com.ssginc.unnie.admin.dto.report;

import com.ssginc.unnie.report.vo.ReportReason;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 관리자 신고 요청 전용 DTO
 */
@Data
@Builder
public class AdminReportRequest {
    private int page; // 현재 페이지
    private Integer reportTargetType; // 신고 컨텐츠 타입 (게시글/댓글/리뷰)
    private String reportStatus; // 처리 상태(미처리 / 처리 / 무시)
    private String memberRole; // 유저 권한
    private String startDate; // yyyy-MM-dd
    private String endDate; // yyyy-MM-dd
}
