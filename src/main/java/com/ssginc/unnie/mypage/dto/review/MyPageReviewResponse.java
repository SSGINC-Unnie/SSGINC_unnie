package com.ssginc.unnie.mypage.dto.review;

import com.ssginc.unnie.review.dto.ReviewResponseBase;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 나의 리뷰 조회 전용 DTO
 */
/**
 * 리뷰 DB select 전용 DTO 클래스
 * 회원은 리뷰 조회 시 상호명, 작성자명, 리뷰 이미지, 리뷰 별점, 리뷰 내용,
 * 리뷰 카테고리, 리뷰 키워드, 리뷰 작성 일시 등의 정보를 볼 수 있습니다.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MyPageReviewResponse extends ReviewResponseBase {

    private String memberNickName; //작성자 명
    private String shopName; //업체명
    private String reviewKeyword; // 리뷰 키워드 (콤마 구분 문자열)

    public MyPageReviewResponse(long reviewId,
                                long reviewMemberId, //작성자 ID
                                long reviewReceiptId, //영수증 ID
                                String reviewImage, //리뷰 대표 이미지
                                int reviewRate, //리뷰 별점
                                String reviewContent, //리뷰 내용
                                LocalDateTime reviewDate, //리뷰 작성 일시
                                String memberNickName, //작성자 명
                                String shopName, //업체명
                                String reviewKeyword) // 리뷰 키워드
    {
        super(reviewId, reviewMemberId, reviewReceiptId, reviewImage, reviewRate, reviewContent, reviewDate);

        this.memberNickName = memberNickName;
        this.shopName = shopName;
        this.reviewKeyword = reviewKeyword;
    }
}
