package com.ssginc.unnie.mypage.service;

import com.ssginc.unnie.mypage.dto.review.MyPageReviewResponse;

import java.util.List;

/**
 * 마이페이지 리뷰 관련 인터페이스
 */
public interface MyPageReviewService {
    /**
     * 리뷰 목록 조회(마이페이지-내가 작성한 리뷰)
     * @param reviewMemberId
     * @return
     */
    List<MyPageReviewResponse> getReviewListById(long reviewMemberId);
}
