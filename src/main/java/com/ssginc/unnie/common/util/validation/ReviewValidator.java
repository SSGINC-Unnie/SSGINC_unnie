package com.ssginc.unnie.common.util.validation;

import com.ssginc.unnie.review.dto.ReviewRequestBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 리뷰 유효성 검증 클래스 (리팩토링 버전)
 */
@Slf4j
@Component
public class ReviewValidator implements Validator<ReviewRequestBase> {

    private static final int MIN_REVIEW_LENGTH = 10;
    private static final int MAX_REVIEW_LENGTH = 400;
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;
    private static final int MIN_KEYWORDS = 1;
    private static final int MAX_KEYWORDS = 5;

    @Override
    public boolean validate(ReviewRequestBase review) {
        if (review == null) {
            log.error("ReviewRequestBase가 null입니다.");
            return false;
        }
        return validateReviewContent(review.getReviewContent()) &&
                validateReviewRate(review.getReviewRate()) &&
                validateReviewKeywords(review.getKeywordId());
    }

    /**
     * 리뷰 내용이 MIN_REVIEW_LENGTH자 이상 MAX_REVIEW_LENGTH자 이하인지 검증
     */
    private boolean validateReviewContent(String reviewContent) {
        if (reviewContent == null || reviewContent.trim().isEmpty()) {
            log.error("리뷰 내용을 작성해주세요.");
            return false;
        }
        int length = reviewContent.trim().length();
        if (length < MIN_REVIEW_LENGTH || length > MAX_REVIEW_LENGTH) {
            log.error("리뷰 길이가 유효하지 않습니다. (현재 길이: {}, 허용 범위: {}~{}자)",
                    length, MIN_REVIEW_LENGTH, MAX_REVIEW_LENGTH);
            return false;
        }
        return true;
    }

    /**
     * 리뷰 별점이 MIN_RATING 이상 MAX_RATING 이하인지 검증
     */
    private boolean validateReviewRate(int reviewRate) {
        if (reviewRate < MIN_RATING || reviewRate > MAX_RATING) {
            log.error("별점이 유효하지 않습니다. (입력값: {}, 허용 범위: {}~{})",
                    reviewRate, MIN_RATING, MAX_RATING);
            return false;
        }
        return true;
    }

    /**
     * 리뷰 키워드가 최소 MIN_KEYWORDS개 이상 MAX_KEYWORDS개 이하인지 검증
     */
    private boolean validateReviewKeywords(List<Integer> keywordId) {
        if (keywordId == null || keywordId.isEmpty()) {
            log.error("최소 하나 이상의 키워드를 선택해야 합니다.");
            return false;
        }
        int count = keywordId.size();
        if (count < MIN_KEYWORDS || count > MAX_KEYWORDS) {
            log.error("키워드 개수가 유효하지 않습니다. (입력값: {}, 허용 범위: {}~{})",
                    count, MIN_KEYWORDS, MAX_KEYWORDS);
            return false;
        }
        return true;
    }
}
