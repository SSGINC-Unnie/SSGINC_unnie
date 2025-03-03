package com.ssginc.unnie.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 리뷰 DB select 전용 DTO 클래스
 * 회원은 리뷰 조회 시 상호명, 작성자명, 리뷰 이미지, 리뷰 별점, 리뷰 내용,
 * 리뷰 카테고리, 리뷰 키워드, 리뷰 작성 일시 등의 정보를 볼 수 있습니다.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ReviewGetResponse extends ReviewResponseBase {

//    private List<String> reviewKeywords; // 리뷰 키워드 목록
    private String memberNickName; //작성자 명
    private String shopName;

    public ReviewGetResponse(long reviewId,
                             long reviewMemberId, //작성자 ID
                             long reviewReceiptId, //영수증 ID
                             String reviewImage, //리뷰 대표 이미지
                             int reviewRate, //리뷰 별점
                             String reviewContent, //리뷰 내용
                             LocalDateTime reviewDate, //리뷰 작성 일시
//                             List<String> reviewKeywords,
                             String memberNickName,
                             String shopName)
    {
        super(reviewId, reviewMemberId, reviewReceiptId, reviewImage, reviewRate, reviewContent, reviewDate);
//        this.reviewKeywords = reviewKeywords;
        this.memberNickName = memberNickName;
        this.shopName = shopName;
    }
}
