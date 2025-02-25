package com.ssginc.unnie.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 리뷰 요청 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequest {
    private long reviewId;          // 리뷰 번호
    private long reviewMemberId;    // 작성자 번호
    private long reviewReceiptId;   // 영수증 번호
    private String reviewImage;     // 리뷰 이미지
    private int reviewRate;         // 리뷰 별점
    private String reviewContent;   // 리뷰 내용
}
