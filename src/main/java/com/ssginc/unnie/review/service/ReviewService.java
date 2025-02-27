package com.ssginc.unnie.review.service;

import com.ssginc.unnie.review.dto.ReviewCreateRequest;
import com.ssginc.unnie.review.dto.ReviewGetResponse;
import com.ssginc.unnie.review.dto.ReviewUpdateRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewService {

    /**
     * 리뷰 저장
     * @param reviewCreateRequest 리뷰 등록에 필요한 정보를 담은 DTO
     * @return 저장된 리뷰의 ID
     */
    long createReview(ReviewCreateRequest reviewCreateRequest);

    /**
     * 리뷰 조회
     * @param reviewId 조회할 리뷰의 ID
     * @return 해당 리뷰의 상세 정보를 담은 DTO
     */
    ReviewGetResponse getReviewById(long reviewId);

    /**
     * 키워드 조회
     * @param reviewId 조회할 리뷰의 ID
     * @return 키워드를 담은 DTO
     */
    List<String> selectReviewKeywordsByReviewId(long reviewId);

    /**
     * 리뷰 수정
     * @param reviewUpdateRequest 리뷰 수정에 필요한 정보를 담은 DTO
     * @return 수정된 리뷰의 ID
     */
    long updateReview(ReviewUpdateRequest reviewUpdateRequest);


    /**
     * 키워드 삭제
     * @param reviewId
     * @return 삭제 결과
     */
    int deleteReviewKeywords(long reviewId);

    @Transactional
    long softDeleteReview(long reviewId, long memberId);


//    List<ReviewGetResponse> getAllReviews();
}
