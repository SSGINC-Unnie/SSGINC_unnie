package com.ssginc.unnie.mypage.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieReviewException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.mypage.dto.review.MyPageReviewResponse;
import com.ssginc.unnie.mypage.mapper.MyPageReviewMapper;
import com.ssginc.unnie.mypage.service.MyPageReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 마이페이지 나의 리뷰 관련 인터페이스 구현 클래스
 */
@Service
@RequiredArgsConstructor
public class MyPageReviewServiceImpl implements MyPageReviewService {
    private final MyPageReviewMapper myPageReviewMapper;

    /**
     * 리뷰 목록 조회(마이페이지-내가 작성한 리뷰)
     * @param reviewMemberId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<MyPageReviewResponse> getReviewListById(long reviewMemberId) {
        // 리뷰 작성자(reviewMemberId)에 해당하는 리뷰 목록을 조회합니다.
        List<MyPageReviewResponse> reviews = myPageReviewMapper.getReviewListById(reviewMemberId);

        // 조회된 리뷰 목록이 없으면 예외 처리 (필요에 따라 빈 리스트를 반환할 수도 있습니다.)
        if (reviews == null || reviews.isEmpty()) {
            throw new UnnieReviewException(ErrorCode.REVIEW_NOT_FOUND);
        }

        return reviews;
    }

}
