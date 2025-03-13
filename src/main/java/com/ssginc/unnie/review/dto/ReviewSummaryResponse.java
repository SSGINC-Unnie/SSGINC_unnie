package com.ssginc.unnie.review.dto;

import lombok.Data;

/**
 * 요약 결과 반환
 */
@Data
public class ReviewSummaryResponse {
    private long shopId;
    private String reviewSummary; // 요약된 내용
}
