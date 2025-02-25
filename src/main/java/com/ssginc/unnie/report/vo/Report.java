package com.ssginc.unnie.report.vo;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 신고 VO 클래스
 */

@Getter
public class Report {
    private long reportId; // 신고 식별 번호
    private int reportTargetType; // 신고 컨텐츠 타입(1 : 게시글 / 2: 댓글 / 3: 리뷰)
    private long reportTargetId; // 신고 컨텐츠 식별 번호
    private ReportReason reportReason; // 신고 사유
    private String reportReasonDetailed; // 신고 상세 내용
    private LocalDateTime reportCreatedAt; // 신고일
    private LocalDateTime reportResolvedAt; // 신고 처리 일시
    private int reportStatus; // 처리 상태(0 : 미처리 / 1 : 처리 / 2 : 무시)
    private long reportMemberId; // 신고자 식별 번호
}
