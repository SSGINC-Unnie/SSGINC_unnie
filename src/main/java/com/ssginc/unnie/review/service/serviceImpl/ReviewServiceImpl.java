package com.ssginc.unnie.review.service.serviceImpl;

import com.ssginc.unnie.review.dto.ReviewRequest;
import com.ssginc.unnie.review.dto.ReviewResponse;
import com.ssginc.unnie.review.mapper.ReviewMapper;
import com.ssginc.unnie.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewMapper reviewMapper;

    @Transactional
    @Override
    public ReviewResponse saveReview(ReviewRequest reviewRequest) {
        // 리뷰를 DB에 저장 (insert 시 자동 생성된 reviewId가 reviewRequest에 세팅된다고 가정)
        reviewMapper.insertReview(reviewRequest);
        // 저장된 리뷰의 ID를 이용하여 DB에서 전체 정보를 조회해 반환
        return reviewMapper.findReviewById(reviewRequest.getReviewId());
    }

    @Override
    public ReviewResponse findReviewById(Long reviewId) {
        return reviewMapper.findReviewById(reviewId);
    }
}
