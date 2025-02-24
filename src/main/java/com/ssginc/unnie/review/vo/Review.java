package com.ssginc.unnie.review.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 리뷰 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {
    private long reviewId; //리뷰 번호
    private long reviewMemberId; //작성자 번호
    private long reviewReceiptId; //영수증 번호
    private String reviewImage; //리뷰 이미지
    private int reviewRate; //리뷰 별점
    private String reviewContent; //리뷰 내용
    private LocalDateTime reviewDate; //리뷰 작성 일시
    private String reviewSummary;
    private int reviewEmotionalScore; //리뷰 감정 분석 점수
}
