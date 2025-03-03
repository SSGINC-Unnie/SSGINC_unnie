package com.ssginc.unnie.mypage.mapper;

import com.ssginc.unnie.mypage.dto.review.MyPageReviewResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyPageReviewMapper {
    /**
     * 리뷰 목록 조회(마이페이지-내가 작성한 리뷰)
     */
    List<MyPageReviewResponse> getReviewListById(@Param("reviewMemberId") long reviewMemberId);
}