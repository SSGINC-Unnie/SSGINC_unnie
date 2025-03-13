package com.ssginc.unnie.review.dto;

import lombok.Data;

/**
 * 특정 업체의 리뷰 요약을 요청할 때 사용
 */
@Data
public class ReviewSummaryRequest {
    private long shopId; // 요약 대상 Shop ID
}
