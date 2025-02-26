package com.ssginc.unnie.review.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.review.dto.ReviewCreateRequest;
import com.ssginc.unnie.review.dto.ReviewGetResponse;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.review.dto.ReviewUpdateRequest;
import com.ssginc.unnie.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 등록
     * - 클라이언트는 ReviewCreateRequest JSON과 함께 (선택적으로) keywordIds 파라미터(콤마 구분 문자열)를 전달합니다.
     */
    @PostMapping("")
    public ResponseEntity<ResponseDto<Map<String, String>>> createReview(
            @RequestBody ReviewCreateRequest reviewCreateRequest,
            @RequestParam(value = "keywordIds", required = false) String keywordIdsStr) {

        // 키워드 ID 문자열(콤마 구분)을 List<Integer>로 변환 (필요한 경우)
        reviewCreateRequest.setKeywordIds(parseKeywordIds(keywordIdsStr));

        long reviewId = reviewService.createReview(reviewCreateRequest);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "리뷰 작성에 성공했습니다.", Map.of("reviewId", String.valueOf(reviewId)))
        );
    }

    /**
     * 리뷰 상세 조회
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getReview(@PathVariable("reviewId") long reviewId) {
        ReviewGetResponse review = reviewService.getReviewById(reviewId);
        if (review == null) {
            throw new RuntimeException("해당 리뷰를 찾을 수 없습니다. reviewId: " + reviewId);
        }
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "리뷰 조회 성공", Map.of("review", review))
        );
    }

    /**
     * 리뷰 키워드 조회
     * URL 예시: GET /api/review/keywords/1
     */
    @GetMapping("/keywords/{reviewId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getReviewKeywords(@PathVariable("reviewId") long reviewId) {
        List<String> keywords = reviewService.selectReviewKeywordsByReviewId(reviewId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "키워드 조회 성공", Map.of("keywords", keywords))
        );
    }

    /**
     * 리뷰 수정
     * - 리뷰 수정 시, 리뷰의 기본 정보와 함께 선택적으로 키워드 수정이 가능합니다.
     * - 키워드 수정을 원하면 keywordIds 파라미터(콤마 구분 문자열)를 전달하고,
     *   전달하지 않으면 기존 키워드 정보가 그대로 유지됩니다.
     *
     * @param reviewId 수정할 리뷰의 ID
     * @param reviewUpdateRequest 리뷰 수정 요청 DTO
     * @param keywordIdsStr 수정할 키워드 ID (콤마 구분 문자열, 선택적)
     * @return 수정된 리뷰의 ID와 성공 메시지를 포함한 ResponseEntity
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> updateReview(
            @PathVariable("reviewId") long reviewId,
            @RequestBody ReviewUpdateRequest reviewUpdateRequest,
            @RequestParam(value = "keywordIds", required = false) String keywordIdsStr) {

        // 키워드 수정 의사가 있으면 파라미터 변환, 없으면 null 유지
        reviewUpdateRequest.setKeywordIds(parseKeywordIds(keywordIdsStr));
        // URL로 전달받은 reviewId를 DTO에 설정
        reviewUpdateRequest.setReviewId(reviewId);

        long updatedReviewId = reviewService.updateReview(reviewUpdateRequest);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "리뷰 수정에 성공했습니다.", Map.of("reviewId", String.valueOf(updatedReviewId)))
        );
    }

    /**
     * 콤마 구분된 키워드 ID 문자열을 List<Integer>로 변환하는 헬퍼 메서드.
     * 입력이 없으면 null을 반환하여, 키워드 수정 없이 기존 키워드를 유지합니다.
     */
    private List<Integer> parseKeywordIds(String keywordIdsStr) {
        if (keywordIdsStr != null && !keywordIdsStr.trim().isEmpty()) {
            return Arrays.stream(keywordIdsStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 리뷰 소프트 딜리트 API
     *
     * @param reviewId         삭제할 리뷰의 ID
     * @param memberPrincipal  현재 로그인한 사용자 정보
     * @return 삭제 결과 응답 DTO
     */
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> softDeleteReview(
            @PathVariable("reviewId") long reviewId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        long memberId = memberPrincipal.getMemberId();
        log.info("리뷰 삭제 요청. reviewId: {}, 요청자: {}", reviewId, memberId);

        long deletedReviewId = reviewService.softDeleteReview(reviewId, memberId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "리뷰 삭제 성공", Map.of("reviewId", deletedReviewId))
        );
    }
}
