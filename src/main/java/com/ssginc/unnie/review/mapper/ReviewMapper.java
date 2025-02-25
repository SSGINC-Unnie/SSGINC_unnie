package com.ssginc.unnie.review.mapper;

import com.ssginc.unnie.review.dto.ReviewRequest;
import com.ssginc.unnie.review.dto.ReviewResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewMapper {
    void insertReview(ReviewRequest reviewRequest);

    ReviewResponse findReviewById(@Param("reviewId")Long reviewId);
}
