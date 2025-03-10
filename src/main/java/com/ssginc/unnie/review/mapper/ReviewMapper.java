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
     */
    int insertReviewKeywordsForCreate(ReviewCreateRequest reviewCreateRequest);

    /**
     * 단일 리뷰 상세 조회: review, member, receipt, shop 테이블을 JOIN하여 ReviewGetResponse DTO로 반환한다.
     */
    ReviewGetResponse getReviewById(@Param("reviewId") long reviewId);

    /**
     * 특정 리뷰의 키워드 목록 조회: review_keyword와 keyword 테이블을 JOIN하여 키워드 문자열 목록을 반환한다.
     */
    List<String> selectReviewKeywordsByReviewId(@Param("reviewId") long reviewId);

    /**
     * 리뷰 수정: review 테이블 업데이트
     */
    int updateReview(ReviewUpdateRequest reviewUpdateRequest);

    /**
     * 기존 리뷰의 키워드 삭제
     */
    int deleteReviewKeywords(@Param("reviewId") long reviewId);

    /**
     * 리뷰-키워드 연결 정보 등록 (수정용): review_keyword 테이블에 (keyword_id, review_id)를 삽입한다.
     */
    int insertReviewKeywordsForUpdate(ReviewUpdateRequest reviewUpdateRequest);

    /**
     * 리뷰 존재 여부 확인: EXISTS 함수를 사용하여 리뷰가 존재하는지 확인한다.
     */
    boolean existsReview(@Param("reviewId") long reviewId);

    /**
     * 리뷰 삭제 시 특정 리뷰가 존재하는지 + 삭제를 요청한 사용자가 작성자인지 검증한다.
     */
    Integer checkReviewAndAuthor(@Param("reviewId") long reviewId, @Param("reviewMemberId") long reviewMemberId);

    /**
     * 리뷰 소프트 딜리트: review_status -> User Delete: 1, Admin Delete: 2
     */
    int softDeleteReview(@Param("reviewId") long reviewId, @Param("reviewStatus") int reviewStatus);

    /**
     * 업체의 리뷰 목록 조회
     * 정렬: 최신순, 오래된순, 키워드별 조회
     * 페이지네이션: 무한스크롤
     * @param shopId 업체 ID
     * @param keyword 필터링할 키워드
     * @param sortType 정렬 방식 ('newest', 'oldest')
     * @param offset 페이지네이션 시작 값
     * @param limit 조회 건수
     * @return 리뷰 목록
     */
    List<ReviewGetResponse> getReviewListByShop(
            @Param("shopId") long shopId,
            @Param("keyword") String keyword,
            @Param("sortType") String sortType,
            @Param("offset") int offset,
            @Param("limit") int limit);

    int getReviewCountByShop(@Param("shopId") long shopId,
                             @Param("keyword") String keyword);
}
