package com.ssginc.unnie.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 리뷰 DB select 비회원 전용 DTO 클래스
 * 비회원은 리뷰 조회 시 상호명, 작성자명, 리뷰 이미지, 리뷰 별점,
 * 리뷰 키워드, 리뷰 작성 일시 등의 정보를 볼 수 있습니다. (리뷰 내용은 블러 처리)
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ReviewGuestGetResponse extends ReviewResponseBase {

    private String memberNickName; // 작성자 명
    private String shopName;
    private String reviewKeyword; // 리뷰 키워드 (콤마 구분 문자열)

    public ReviewGuestGetResponse(long reviewId,
                                  long reviewMemberId, // 작성자 ID
                                  long reviewReceiptId, // 영수증 ID
                                  String reviewImage, // 리뷰 대표 이미지
                                  int reviewRate, // 리뷰 별점
                                  String reviewContent, // 리뷰 내용
                                  LocalDateTime reviewDate, // 리뷰 작성 일시
                                  String memberNickName,
                                  String shopName,
                                  String reviewKeyword) {
        super(reviewId, reviewMemberId, reviewReceiptId, reviewImage, reviewRate, reviewContent, reviewDate);
        this.memberNickName = memberNickName;
        this.shopName = shopName;
        this.reviewKeyword = reviewKeyword;
    }
}
