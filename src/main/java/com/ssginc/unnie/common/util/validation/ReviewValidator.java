package com.ssginc.unnie.common.util.validation;

import com.ssginc.unnie.review.dto.ReviewRequestBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 리뷰 유효성 검증 클래스
 */

@Slf4j  // 로그 추가
@Component
public class ReviewValidator implements Validator <ReviewRequestBase>{

    @Override
    public boolean validate(ReviewRequestBase review) {
        if (review == null) {
            log.error("ReviewRequestBase is null");
            return false;
        }
        boolean contentValid = validateReviewContent(review.getReviewContent());
        boolean rateValid = validateReviewRate(review.getReviewRate());
        boolean keywordsValid = validateReviewKeywords(review.getKeywordIds());
        return contentValid && rateValid && keywordsValid;
    }

    //리뷰 이미지 첨부 여부 검증

    /**
     * 리뷰 내용이 10글자 이상 400글자 이하인지 검증하는 메서드
     *
     * @param reviewContent 검증할 리뷰 내용
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateReviewContent(String reviewContent) {
        if (reviewContent == null) {
            log.error("리뷰 내용을 작성해주세요!");
            return false;
        }
        int length = reviewContent.trim().length();
        if (length < 10 || length > 400) {
            log.error("리뷰 작성 길이가 올바르지 않습니다. " + length + " (10글자에서 400글자 이내로 작성해주십시오.)");
            return false;
        }
        return true;
    }

    /**
     * 리뷰 별점이 1 이상 5 이하인지 검증하는 메서드
     *
     * @param reviewRate 검증할 리뷰 별점
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateReviewRate(int reviewRate) {
        if (reviewRate < 1 || reviewRate > 5) {
            log.error("별점을 다시 선택해주세요! " + reviewRate + "(최소 1개 이상, 최대 5개 이하)");
            return false;
        }
        return true;
    }

    /**
     * 리뷰 키워드가 최소 1개 이상 5개 이하로 선택되었는지 검증하는 메서드
     *
     * @param keywordIds 검증할 키워드 ID 리스트
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateReviewKeywords(List<Integer> keywordIds) {
        if (keywordIds == null) {
            log.error("Review keywords is null");
            return false;
        }
        int count = keywordIds.size();
        if (count < 1 || count > 5) {
            log.error("Review keywords count invalid: " + count + " (should be between 1 and 5)");
            return false;
        }
        return true;
    }

}
