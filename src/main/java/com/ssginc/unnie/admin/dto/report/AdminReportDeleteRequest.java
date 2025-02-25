package com.ssginc.unnie.admin.dto.report;

import lombok.Getter;
import lombok.Setter;

/**
 * 신고 컨텐츠 삭제 요청 전용 DTO
 */

@Getter
@Setter
public class AdminReportDeleteRequest {
    private long reportId; // 신고 식별 번호
    private int reportTargetType; // 신고 컨텐츠 타입(1 : 게시글 / 2: 댓글 / 3: 리뷰)
    private long reportTargetId; // 신고 컨텐츠 식별 번호
    private long reportMemberId; // 신고자 식별 번호
}
