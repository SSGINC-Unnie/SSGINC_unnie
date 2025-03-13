package com.ssginc.unnie.review.service;

import com.ssginc.unnie.review.dto.ReviewCreateRequest;
import com.ssginc.unnie.review.dto.ReviewGetResponse;
import com.ssginc.unnie.review.dto.ReviewGuestGetResponse;
import com.ssginc.unnie.review.dto.ReviewUpdateRequest;

import java.util.List;

public interface ReviewService {

    /**
     * 리뷰 저장
     * @param reviewCreateRequest 리뷰 등록에 필요한 정보를 담은 DTO
     * @return 저장된 리뷰의 ID
     */
    long createReview(ReviewCreateRequest reviewCreateRequest);

    /**
     * 리뷰 조회
     * @param reviewId 조회할 리뷰의 ID
     * @return 해당 리뷰의 상세 정보를 담은 DTO
     */
    ReviewGetResponse getReviewById(long reviewId);

    /**
     * 키워드 조회
     * @param reviewId 조회할 리뷰의 ID
     * @return 키워드를 담은 DTO
     */
    List<String> selectReviewKeywordsByReviewId(long reviewId);

    /**
     * 리뷰 수정
     * @param reviewUpdateRequest 리뷰 수정에 필요한 정보를 담은 DTO
     * @return 수정된 리뷰의 ID
     */
    long updateReview(ReviewUpdateRequest reviewUpdateRequest);


    /**
     * 키워드 삭제
     * @param reviewId
     * @return 삭제 결과
     */
    int deleteReviewKeywords(long reviewId);

    /**
     * 리뷰 삭제(Soft Delete)
     * @param reviewId
     * @param reviewStatus
     * @return
     */
    long softDeleteReview(long reviewId, int reviewStatus);

    /**
     * 업체 리뷰 목록 조회 (회원 전용)
     * @param shopId  업체 ID
     * @param keyword 필터링할 키워드 (없으면 null 또는 빈 문자열)
     * @param sortType 정렬 방식 ('newest', 'oldest')
     * @param offset  페이지네이션 시작 값
     * @param limit   조회 건수
     * @return 리뷰 목록
     */
    List<ReviewGetResponse> getReviewListByShop(long shopId, String keyword, String sortType, int offset, int limit);

    /**
     * 업체 리뷰 목록 조회 (비회원 전용)
     * @param shopId  업체 ID
     * @param keyword 필터링할 키워드 (없으면 null 또는 빈 문자열)
     * @param sortType 정렬 방식 ('newest', 'oldest')
     * @param offset  페이지네이션 시작 값
     * @param limit   조회 건수
     * @return 리뷰 목록
     */
    List<ReviewGuestGetResponse> getReviewListByShopGuest(long shopId, String keyword, String sortType, int offset, int limit);

    /**
     * 업체 리뷰 개수 조회
     * @param shopId 업체 ID
     * @param keyword 필터링할 키워드 (없으면 빈 문자열)
     * @return 해당 업체의 전체 리뷰 개수
     */
    int getReviewCountByShop(long shopId, String keyword);

    /**
     * 특정 shopId의 리뷰를 모두 조회하여 OpenAI 요약 후 DB에 저장
     * @param shopId 대상 샵 ID
     * @return 요약 결과 문자열
     */
    String summarizeAndSave(long shopId);
}
