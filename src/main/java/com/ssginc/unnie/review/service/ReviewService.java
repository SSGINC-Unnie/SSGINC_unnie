package com.ssginc.unnie.review.service;

import com.ssginc.unnie.review.dto.ReviewRequest;
import com.ssginc.unnie.review.dto.ReviewResponse;

public interface ReviewService {
    /**
     * 리뷰 저장
     */
    ReviewResponse saveReview(ReviewRequest reviewRequest);

    /**
     * 리뷰 조회
     */
    ReviewResponse findReviewById(Long reviewId);
}
