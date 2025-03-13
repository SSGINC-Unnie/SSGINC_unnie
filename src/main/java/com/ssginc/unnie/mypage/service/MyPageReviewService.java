package com.ssginc.unnie.mypage.service;

import com.ssginc.unnie.mypage.dto.review.MyPageReviewResponse;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 마이페이지 리뷰 관련 인터페이스
 */
public interface MyPageReviewService {

    /**
     * 리뷰 목록 조회(마이페이지-내가 작성한 리뷰)
     * @param reviewMemberId
     * @param sortType
     * @param offset
     * @param limit
     * @return
     */
    List<MyPageReviewResponse> getReviewListById(
            @Param("reviewMemberId") long reviewMemberId,
            @Param("sortType") String sortType,
            @Param("offset") int offset,
            @Param("limit") int limit);


    /**
     * 작성한 리뷰 카운트(마이페이지-내가 작성한 리뷰)
     * @param reviewMemberId
     * @return
     */
    int getReviewCountById(long reviewMemberId);
}
