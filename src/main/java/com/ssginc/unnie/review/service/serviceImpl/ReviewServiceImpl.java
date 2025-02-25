package com.ssginc.unnie.review.service.serviceImpl;

import com.ssginc.unnie.review.dto.ReviewCreateRequest;
import com.ssginc.unnie.review.dto.ReviewGetResponse;
import com.ssginc.unnie.review.mapper.ReviewMapper;
import com.ssginc.unnie.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    /**
     * 리뷰 저장: 리뷰 테이블에 등록하고, 선택된 키워드가 있다면 review_keyword 테이블에 저장합니다.
     * 트랜잭션 내에서 두 작업이 모두 성공해야 커밋됩니다.
     *
     * @param reviewCreateRequest 리뷰 등록 요청 DTO
     * @return 생성된 리뷰의 ID
     */
    @Override
    @Transactional
    public long createReview(ReviewCreateRequest reviewCreateRequest) {
        // 리뷰 등록: review 테이블에 삽입 후 자동 생성된 reviewId가 reviewCreateRequest에 세팅됨
        reviewMapper.insertReview(reviewCreateRequest);

        // 선택한 키워드가 있을 경우 review_keyword 테이블에 연결 정보 등록
        if (reviewCreateRequest.getKeywordIds() != null && !reviewCreateRequest.getKeywordIds().isEmpty()) {
            reviewMapper.insertReviewKeywords(reviewCreateRequest);
        }
        return reviewCreateRequest.getReviewId();
    }

    /**
     * 리뷰 조회: reviewId에 해당하는 리뷰 상세 정보를 조회합니다.
     *
     * @param reviewId 조회할 리뷰 ID
     * @return 조회된 리뷰 DTO
     * @throws RuntimeException 조회된 리뷰가 없을 경우 예외 발생
     */
    @Override
    public ReviewGetResponse getReviewById(long reviewId) {
        ReviewGetResponse review = reviewMapper.getReviewById(reviewId);
        if (review == null) {
            throw new RuntimeException("해당 리뷰를 찾을 수 없습니다. reviewId: " + reviewId);
        }
        return review;
    }

    /**
     * 키워드 조회: reviewId에 해당하는 키워드를 조회합니다.
     *
     * @param reviewId 조회할 리뷰 ID
     * @return 조회된 키워드 목록
     * @throws RuntimeException 조회된 키워드가 없을 경우 예외 발생
     */
    @Override
    public List<String> selectReviewKeywordsByReviewId(long reviewId) {
        List<String> keywords = reviewMapper.selectReviewKeywordsByReviewId(reviewId);
        if (keywords == null || keywords.isEmpty()) {
            throw new RuntimeException("해당 리뷰의 키워드를 찾을 수 없습니다. reviewId: " + reviewId);
        }
        return keywords;
    }
}
