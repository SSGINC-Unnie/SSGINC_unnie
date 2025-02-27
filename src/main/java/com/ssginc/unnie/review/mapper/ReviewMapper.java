package com.ssginc.unnie.review.mapper;

import com.ssginc.unnie.review.dto.ReviewCreateRequest;
import com.ssginc.unnie.review.dto.ReviewGetResponse;
import com.ssginc.unnie.review.dto.ReviewUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ReviewMapper {

    /**
     * 리뷰 등록: review 테이블에 데이터를 삽입하고, 생성된 reviewId를 매핑한다.
     */
    int insertReview(ReviewCreateRequest reviewCreateRequest);

    /**
     * 리뷰-키워드 연결 정보 등록 (생성용): review_keyword 테이블에 (keyword_id, review_id)를 삽입한다.
     * ReviewCreateRequest의 keywordIds 리스트와 reviewId를 활용한다.
     */
    int insertReviewKeywordsForCreate(ReviewCreateRequest reviewCreateRequest);

    /**
     * 단일 리뷰 상세 조회: review, member, receipt, shop 테이블을 JOIN하여 ReviewGetResponse DTO로 반환.
     */
    ReviewGetResponse getReviewById(@Param("reviewId") long reviewId);

    /**
     * 특정 리뷰의 키워드 목록 조회: review_keyword와 keyword 테이블을 JOIN하여 키워드 문자열 목록을 반환.
     */
    List<String> selectReviewKeywordsByReviewId(@Param("reviewId") long reviewId);

    /**
     * 리뷰 수정: review 테이블 업데이트.
     */
    int updateReview(ReviewUpdateRequest reviewUpdateRequest);

    /**
     * 기존 리뷰의 키워드 삭제.
     */
    int deleteReviewKeywords(@Param("reviewId") long reviewId);

    /**
     * 리뷰-키워드 연결 정보 등록 (수정용): review_keyword 테이블에 (keyword_id, review_id)를 삽입한다.
     * ReviewUpdateRequest의 keywordIds 리스트와 reviewId를 활용한다.
     */
    int insertReviewKeywordsForUpdate(ReviewUpdateRequest reviewUpdateRequest);

    int checkReviewId(long reviewId);

    Integer checkReviewAndAuthor(@Param("reviewId") long reviewId, @Param("memberId") long memberId);

    int softDeleteReview(long reviewId);
}
