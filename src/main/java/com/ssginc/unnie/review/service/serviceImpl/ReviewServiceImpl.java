package com.ssginc.unnie.review.service.serviceImpl;

import com.ssginc.unnie.review.dto.ReviewCreateRequest;
import com.ssginc.unnie.review.dto.ReviewGetResponse;
import com.ssginc.unnie.review.dto.ReviewUpdateRequest;
import com.ssginc.unnie.review.mapper.ReviewMapper;
import com.ssginc.unnie.review.service.ReceiptService;
import com.ssginc.unnie.review.service.ReviewService;
import com.ssginc.unnie.common.util.validation.ReviewValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final ReceiptService receiptService;  // 영수증 인증 검증을 위한 서비스
    private final ReviewValidator reviewValidator;  // 리뷰 유효성 검증을 위한 클래스

    /**
     * 리뷰 저장: review 테이블과 review_keyword 테이블에 데이터를 등록합니다.
     * 트랜잭션 내에서 두 작업이 모두 성공해야 커밋됩니다.
     *
     * @param reviewCreateRequest 리뷰 등록 요청 DTO
     * @return 생성된 리뷰의 ID
     */
    @Override
    @Transactional
    public long createReview(ReviewCreateRequest reviewCreateRequest) {

        // 1. 영수증 인증 검증: 영수증이 인증되지 않았다면 리뷰 작성을 차단합니다.
        if (!receiptService.isReceiptVerified(reviewCreateRequest.getReviewReceiptId())) {
            throw new RuntimeException("영수증 인증이 필요합니다. 인증되지 않은 영수증입니다.");
        }

        // 2. 리뷰 데이터 유효성 검증
        if (!reviewValidator.validate(reviewCreateRequest)) {
            throw new RuntimeException("리뷰 데이터가 유효하지 않습니다.");
        }

        // 3. 리뷰 등록 (review 테이블)
        reviewMapper.insertReview(reviewCreateRequest);

        // 4. 선택한 키워드가 있을 경우 review_keyword 등록 (생성용)
        if (reviewCreateRequest.getKeywordIds() != null && !reviewCreateRequest.getKeywordIds().isEmpty()) {
            reviewMapper.insertReviewKeywordsForCreate(reviewCreateRequest);
        }
        return reviewCreateRequest.getReviewId();
    }

    /**
     * 리뷰 조회: reviewId에 해당하는 리뷰 상세 정보를 반환합니다.
     *
     * @param reviewId 조회할 리뷰 ID
     * @return 조회된 리뷰 DTO
     */
    @Override
    public ReviewGetResponse getReviewById(long reviewId) {
        ReviewGetResponse review = reviewMapper.getReviewById(reviewId);
        if (review == null) {
            // 추후 ExceptionHandler를 통한 404 응답 처리 고려
            throw new RuntimeException("해당 리뷰를 찾을 수 없습니다. reviewId: " + reviewId);
        }
        return review;
    }

    /**
     * 키워드 조회: reviewId에 해당하는 리뷰의 키워드 목록을 반환합니다.
     *
     * @param reviewId 조회할 리뷰 ID
     * @return 키워드 문자열 목록
     */
    @Override
    public List<String> selectReviewKeywordsByReviewId(long reviewId) {
        List<String> keywords = reviewMapper.selectReviewKeywordsByReviewId(reviewId);
        if (keywords == null || keywords.isEmpty()) {
            throw new RuntimeException("해당 리뷰의 키워드를 찾을 수 없습니다. reviewId: " + reviewId);
        }
        return keywords;
    }

    /**
     * 리뷰 수정: review 테이블 업데이트와 함께, 기존 키워드 정보를 삭제한 후 새로운 키워드 정보를 삽입합니다.
     * 키워드 수정 의사가 없으면 DTO의 keywordIds가 null이어야 하므로 기존 키워드를 유지합니다.
     * 모든 작업은 트랜잭션 내에서 실행됩니다.
     *
     * @param reviewUpdateRequest 리뷰 수정 요청 DTO
     * @return 수정된 리뷰의 ID
     */
    @Override
    @Transactional
    public long updateReview(ReviewUpdateRequest reviewUpdateRequest) {
        // 1. 리뷰 수정 데이터 유효성 검증
        if (!reviewValidator.validate(reviewUpdateRequest)) {
            throw new RuntimeException("리뷰 데이터가 유효하지 않습니다.");
        }

        // 2. 리뷰 테이블 업데이트
        reviewMapper.updateReview(reviewUpdateRequest);

        // 3. 키워드 수정 의사가 있을 경우에만 처리 (null이면 기존 키워드 유지)
        if (reviewUpdateRequest.getKeywordIds() != null) {
            reviewMapper.deleteReviewKeywords(reviewUpdateRequest.getReviewId());
            reviewMapper.insertReviewKeywordsForUpdate(reviewUpdateRequest);
        }
        return reviewUpdateRequest.getReviewId();
    }

    /**
     * 키워드 삭제: 리뷰 삭제 시 review_keyword 테이블의 연결 정보를 삭제합니다.
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @return 삭제된 행의 수
     */
    @Override
    @Transactional
    public int deleteReviewKeywords(long reviewId) {
        return reviewMapper.deleteReviewKeywords(reviewId);
    }
}
