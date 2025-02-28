package com.ssginc.unnie.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 리뷰 요청 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ReviewRequestBase {
    private long reviewId;          // 리뷰 번호
    private long reviewMemberId;    // 작성자 번호
    private long reviewReceiptId;   // 영수증 번호
    private String reviewImage;     // 리뷰 대표 이미지
    private int reviewRate;         // 리뷰 별점
    private String reviewContent;   // 리뷰 내용
    private List<Integer> keywordIds; //리뷰 키워드
}
