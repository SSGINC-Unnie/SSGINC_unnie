package com.ssginc.unnie.review.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieReviewException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.mypage.dto.review.MyPageReviewResponse;
import com.ssginc.unnie.review.dto.ReviewCreateRequest;
import com.ssginc.unnie.review.dto.ReviewGetResponse;
import com.ssginc.unnie.review.dto.ReviewRequestBase;
import com.ssginc.unnie.review.dto.ReviewUpdateRequest;
import com.ssginc.unnie.review.mapper.ReviewMapper;
import com.ssginc.unnie.review.service.ReceiptService;
import com.ssginc.unnie.review.service.ReviewService;
import com.ssginc.unnie.common.util.validation.Validator;
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
    private final ReceiptService receiptService;  // 영수증 인증 검증 서비스
    private final Validator<ReviewRequestBase> reviewValidator;  // ReviewValidator (ReviewRequestBase 타입)

    /**
     * 리뷰 저장: review 테이블과 review_keyword 테이블에 데이터를 등록합니다.
     * 트랜잭션 내에서 두 작업이 모두 성공해야 커밋됩니다.
     *
     * @param reviewCreateRequest 리뷰 등록 요청 DTO
     * @return 생성된 리뷰의 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createReview(ReviewCreateRequest reviewCreateRequest) {

        // 1. 영수증 인증 검증: 영수증이 인증되지 않았다면 리뷰 작성을 차단합니다.
        if (!receiptService.isReceiptVerified(reviewCreateRequest.getReviewReceiptId())) {
            throw new UnnieReviewException(ErrorCode.INVALID_RECEIPT);
        }

        // 2. 리뷰 데이터 유효성 검증
        if (!reviewValidator.validate(reviewCreateRequest)) {
            // 개별 조건 재검증하여 구체적인 에러 코드 적용
            String content = reviewCreateRequest.getReviewContent();
            if (content == null || content.trim().isEmpty()) {
                throw new UnnieReviewException(ErrorCode.REVIEW_CONTENT_REQUIRED);
            }
            int length = content.trim().length();
            if (length < 10 || length > 400) {
                throw new UnnieReviewException(ErrorCode.REVIEW_LENGTH_EXCEEDED);
            }
            int rate = reviewCreateRequest.getReviewRate();
            if (rate < 1) {
                throw new UnnieReviewException(ErrorCode.REVIEW_RATE_REQUIRED);
            }
            if (rate > 5) {
                throw new UnnieReviewException(ErrorCode.REVIEW_RATE_EXCEEDED);
            }
            List<Integer> keywords = reviewCreateRequest.getKeywordIds();
            if (keywords == null || keywords.isEmpty()) {
                throw new UnnieReviewException(ErrorCode.REVIEW_KEYWORDS_REQUIRED);
            }
            if (keywords.size() > 5) {
                throw new UnnieReviewException(ErrorCode.REVIEW_KEYWORDS_EXCEEDED);
            }
            // 기본적으로
            throw new UnnieReviewException(ErrorCode.INVALID_REVIEW_CONTENT);
        }

        // 3. 리뷰 등록 (review 테이블)
        int insertCount = reviewMapper.insertReview(reviewCreateRequest);
        if (insertCount == 0) {
            // 리뷰 등록이 실패하면(예: 중복 등) 추가 에러 코드 적용 가능
            throw new UnnieReviewException(ErrorCode.DUPLICATE_REVIEW);
        }

        // 4. review_keyword 등록 (키워드는 Validator에서 검증되었으므로 바로 진행)
        int keywordInsertCount = reviewMapper.insertReviewKeywordsForCreate(reviewCreateRequest);
        if (keywordInsertCount == 0) {
            throw new UnnieReviewException(ErrorCode.KEYWORDS_NOT_FOUND);
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
            throw new UnnieReviewException(ErrorCode.REVIEW_NOT_FOUND);
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
            throw new UnnieReviewException(ErrorCode.KEYWORDS_NOT_FOUND);
        }
        return keywords;
    }

    /**
     * 리뷰 수정: review 테이블 업데이트와 함께, 기존 키워드 정보를 삭제한 후 새로운 키워드 정보를 삽입합니다.
     * 키워드 수정 의사가 없으면 DTO의 keywordIds가 null이어야 하므로 기존 키워드를 그대로 유지합니다.
     * 모든 작업은 트랜잭션 내에서 실행됩니다.
     *
     * @param reviewUpdateRequest 리뷰 수정 요청 DTO
     * @return 수정된 리뷰의 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updateReview(ReviewUpdateRequest reviewUpdateRequest) {
        // 키워드 수정이 포함된 경우, 별도로 키워드 조건 검증
        if (reviewUpdateRequest.getKeywordIds() != null) {
            if (reviewUpdateRequest.getKeywordIds().isEmpty()) {
                throw new UnnieReviewException(ErrorCode.REVIEW_KEYWORDS_REQUIRED);
            }
            if (reviewUpdateRequest.getKeywordIds().size() > 5) {
                throw new UnnieReviewException(ErrorCode.REVIEW_KEYWORDS_EXCEEDED);
            }
        }

        // 리뷰 내용 검증
        String content = reviewUpdateRequest.getReviewContent();
        if (content == null || content.trim().isEmpty()) {
            throw new UnnieReviewException(ErrorCode.REVIEW_CONTENT_REQUIRED);
        }
        int length = content.trim().length();
        if (length < 10 || length > 400) {
            throw new UnnieReviewException(ErrorCode.REVIEW_LENGTH_EXCEEDED);
        }

        // 리뷰 별점 검증
        int rate = reviewUpdateRequest.getReviewRate();
        if (rate < 1) {
            throw new UnnieReviewException(ErrorCode.REVIEW_RATE_REQUIRED);
        }
        if (rate > 5) {
            throw new UnnieReviewException(ErrorCode.REVIEW_RATE_EXCEEDED);
        }

        // 추가 유효성 검증은 Validator에 위임 (검증 실패 시 기본 에러 발생)
        if (!reviewValidator.validate(reviewUpdateRequest)) {
            throw new UnnieReviewException(ErrorCode.INVALID_REVIEW_CONTENT);
        }

        // 리뷰 테이블 업데이트 및 업데이트된 행 수 반환
        int updateCount = reviewMapper.updateReview(reviewUpdateRequest);
        if (updateCount == 0) {
            throw new UnnieReviewException(ErrorCode.REVIEW_UPDATE_FAILED);
        }

        // 키워드 수정이 포함된 경우 처리
        if (reviewUpdateRequest.getKeywordIds() != null) {
            reviewMapper.deleteReviewKeywords(reviewUpdateRequest.getReviewId());
            int keywordUpdateCount = reviewMapper.insertReviewKeywordsForUpdate(reviewUpdateRequest);
            if (keywordUpdateCount == 0) {
                throw new UnnieReviewException(ErrorCode.KEYWORDS_NOT_FOUND);
            }
        }
        return reviewUpdateRequest.getReviewId();
    }

    /**
     * 키워드 삭제: 리뷰 수정/삭제 시 review_keyword 테이블의 연결 정보를 삭제합니다.
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @return 삭제된 행의 수
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteReviewKeywords(long reviewId) {
        int deleteCount = reviewMapper.deleteReviewKeywords(reviewId);
        if (deleteCount == 0) {
            throw new UnnieReviewException(ErrorCode.KEYWORDS_NOT_FOUND);
        }
        return deleteCount;
    }

    /**
     * 리뷰 소프트 딜리트: 리뷰 존재 여부를 확인한 후, review_status를 전달받은 상태(1: 사용자 삭제, 2: 관리자 삭제)로 업데이트합니다.
     *
     * @param reviewId     삭제할 리뷰의 ID
     * @param reviewStatus 삭제 상태 (1 또는 2)
     * @return 삭제 처리된 리뷰의 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long softDeleteReview(long reviewId, int reviewStatus) {
        // 리뷰 존재 여부 확인
        boolean exists = reviewMapper.existsReview(reviewId);
        if (!exists) {
            log.error("리뷰가 존재하지 않거나 이미 삭제되었습니다. reviewId: {}", reviewId);
            throw new UnnieReviewException(ErrorCode.REVIEW_NOT_FOUND);
        }
        int deleteCount = reviewMapper.softDeleteReview(reviewId, reviewStatus);
        if (deleteCount == 0) {
            throw new UnnieReviewException(ErrorCode.REVIEW_DELETE_FAILED);
        }
        log.info("리뷰 소프트 딜리트 완료. reviewId: {}, status: {}", reviewId, reviewStatus);
        return reviewId;
    }

}
