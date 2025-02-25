package com.ssginc.unnie.review.controller;

import com.ssginc.unnie.review.dto.KeywordResponse;
import com.ssginc.unnie.review.dto.ReviewCreateRequest;
import com.ssginc.unnie.review.dto.ReviewGetResponse;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.review.service.serviceImpl.ReviewServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewServiceImpl reviewServiceImpl;

    /**
     * 리뷰 등록 API
     * - 리뷰 등록 요청시, ReviewCreateRequest JSON 객체와 함께 (선택적으로) keywordIds 파라미터(콤마 구분된 문자열)를 전달합니다.
     */
    @PostMapping("")
    public ResponseEntity<ResponseDto<Map<String, String>>> createReview(
            @RequestBody ReviewCreateRequest reviewCreateRequest,
            @RequestParam(value = "keywordIds", required = false) String keywordIdsStr) {

        // 키워드 ID 문자열(콤마 구분)을 List<Integer>로 변환
        if (keywordIdsStr != null && !keywordIdsStr.trim().isEmpty()) {
            List<Integer> keywordIds = Arrays.stream(keywordIdsStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
            reviewCreateRequest.setKeywordIds(keywordIds);
        }

        long reviewId = reviewServiceImpl.createReview(reviewCreateRequest);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "리뷰 작성에 성공했습니다.", Map.of("reviewId", String.valueOf(reviewId)))
        );
    }

    /**
     * 리뷰 상세 조회 API
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getReview(@PathVariable("reviewId") long reviewId) {
        ReviewGetResponse review = reviewServiceImpl.getReviewById(reviewId);
        if (review == null) {
            throw new RuntimeException("해당 리뷰를 찾을 수 없습니다. reviewId: " + reviewId);
        }
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "리뷰 조회 성공", Map.of("review", review))
        );
    }

    /**
     * 리뷰 키워드 조회 API
     * URL 예시: GET /api/review/keywords/123
     *
     * @param reviewId 조회할 리뷰 ID
     * @return 리뷰에 연결된 키워드 목록
     */
    @GetMapping("/keywords/{reviewId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getReviewKeywords(@PathVariable("reviewId") long reviewId) {
        List<String> keywords = reviewServiceImpl.selectReviewKeywordsByReviewId(reviewId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "키워드 조회 성공", Map.of("keywords", keywords))
        );
    }

    // 추가적으로 업데이트, 삭제 등의 API를 확장할 수 있습니다.
}
