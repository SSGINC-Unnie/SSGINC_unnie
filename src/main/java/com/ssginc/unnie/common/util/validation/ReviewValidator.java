package com.ssginc.unnie.common.util.validation;

import com.ssginc.unnie.review.dto.ReviewRequestBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 리뷰 유효성 검증 클래스
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ReviewValidator implements Validator<ReviewRequestBase> {

    /**
     * 리뷰 유효성 검증
     */
    @Override
    public boolean validate(ReviewRequestBase review) {
        if (review == null) {
            log.error("ReviewRequestBase가 null입니다.");
            return false;
        }
        return validateReviewContent(review.getReviewContent()) &&
                validateReviewRate(review.getReviewRate()) &&
                validateReviewKeywords(review.getKeywordIds());
    }

    /**
     * 리뷰 내용이 10자 이상 400자 이하인지 검증
     */
    private boolean validateReviewContent(String reviewContent) {
        if (reviewContent == null || reviewContent.trim().isEmpty()) {
            log.error("리뷰 내용을 작성해주세요.");
            return false;
        }
        int length = reviewContent.trim().length();
        if (length < 10 || length > 400) {
            log.error("리뷰 길이가 유효하지 않습니다. (현재 길이: {}, 허용 범위: 10~400자)", length);
            return false;
        }
        return true;
    }

    /**
     * 리뷰 별점이 1 이상 5 이하인지 검증
     */
    private boolean validateReviewRate(int reviewRate) {
        if (reviewRate < 1 || reviewRate > 5) {
            log.error("별점이 유효하지 않습니다. (입력값: {}, 허용 범위: 1~5)", reviewRate);
            return false;
        }
        return true;
    }

    /**
     * 리뷰 키워드가 최소 1개 이상 5개 이하인지 검증
     */
    private boolean validateReviewKeywords(List<Integer> keywordIds) {
        if (keywordIds == null || keywordIds.isEmpty()) {
            log.error("최소 하나 이상의 키워드를 선택해야 합니다.");
            return false;
        }
        int count = keywordIds.size();
        if (count > 5) {
            log.error("키워드 개수가 너무 많습니다. (입력값: {}, 허용 범위: 1~5)", count);
            return false;
        }
        return true;
    }



}
