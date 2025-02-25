package com.ssginc.unnie.review.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 리뷰 DB insert 전용 dto 클래스
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ReviewCreateRequest extends ReviewRequestBase{

    // 추가할 필드: 선택한 키워드들의 ID 목록
    private List<Integer> keywordIds;

    public ReviewCreateRequest(long reviewId, // 리뷰 번호
                               long reviewMemberId, // 작성자 번호
                               long reviewReceiptId, // 영수증 번호
                               String reviewImage, // 리뷰 대표 이미지
                               int reviewRate, // 리뷰 별점
                               String reviewContent, // 리뷰 내용
                               List<Integer> keywordIds) // 선택한 키워드들의 ID 목록
    {
        super(reviewId, reviewMemberId, reviewReceiptId, reviewImage, reviewRate, reviewContent);
        this.keywordIds = keywordIds;
    }
}
