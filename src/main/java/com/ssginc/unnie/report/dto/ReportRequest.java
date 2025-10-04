package com.ssginc.unnie.report.dto;

import lombok.*;

/**
 * REPORT 요청 DTO 클래스
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {
    private long reportId; // 신고 식별 번호
    private Integer reportTargetType; // 신고 컨텐츠 타입(1 : 게시글 / 2: 댓글 / 3: 리뷰)
    private Long reportTargetId; // 신고 컨텐츠 식별 번호
    private String reportReason; // 신고 사유
    private String reportReasonDetailed; // 신고 상세 내용
    private long reportMemberId; // 신고자 식별 번호
}
