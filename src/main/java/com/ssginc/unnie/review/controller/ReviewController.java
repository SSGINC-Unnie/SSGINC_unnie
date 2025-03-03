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
     * 클라이언트는 ReviewCreateRequest JSON과 함께 선택적으로 keywordIds (콤마 구분 문자열)를 전달합니다.
     */
    @PostMapping("")
    public ResponseEntity<ResponseDto<Map<String, String>>> createReview(
            @RequestBody ReviewCreateRequest reviewCreateRequest,
            @RequestParam(value = "keywordIds", required = false) String keywordIdsStr) {

        // 키워드 ID 문자열을 List<Integer>로 변환 (필요한 경우)
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
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "리뷰 조회에 성공했습니다.", Map.of("review", review))
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
                new ResponseDto<>(HttpStatus.OK.value(), "키워드 조회에 성공했습니다.", Map.of("keywords", keywords))
        );
    }

    /**
     * 리뷰 수정
     * 키워드 수정이 필요한 경우 keywordIds (콤마 구분 문자열)를 전달합니다.
     * 전달하지 않으면 기존 키워드가 유지됩니다.
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> updateReview(
            @PathVariable("reviewId") long reviewId,
            @RequestBody ReviewUpdateRequest reviewUpdateRequest,
            @RequestParam(value = "keywordIds", required = false) String keywordIdsStr) {

        reviewUpdateRequest.setKeywordIds(parseKeywordIds(keywordIdsStr));
        reviewUpdateRequest.setReviewId(reviewId);
        long updatedReviewId = reviewService.updateReview(reviewUpdateRequest);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "리뷰 수정에 성공했습니다.", Map.of("reviewId", String.valueOf(updatedReviewId)))
        );
    }

    /**
     * 리뷰 소프트 딜리트
     * User Delete-1, Admin Delete-2
     */
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> softDeleteReview(
            @PathVariable("reviewId") long reviewId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        if (memberPrincipal == null) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        // 권한 정보에서 역할 추출
        String role = memberPrincipal.getAuthorities().isEmpty()
                ? "ROLE_USER"
                : memberPrincipal.getAuthorities().iterator().next().getAuthority();
        int reviewStatus = "ROLE_ADMIN".equalsIgnoreCase(role) ? 2 : 1;
        long memberId = memberPrincipal.getMemberId();
        log.info("리뷰 삭제 요청. reviewId: {}, 요청자: {}, role: {}", reviewId, memberId, role);

        long deletedReviewId = reviewService.softDeleteReview(reviewId, reviewStatus);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "리뷰 삭제에 성공했습니다.", Map.of("reviewId", deletedReviewId))
        );
    }

    /**
     * 콤마 구분된 키워드 ID 문자열을 List<Integer>로 변환하는 헬퍼 메서드.
     * 입력이 없으면 null을 반환하여 키워드 수정 없이 기존 키워드를 유지합니다.
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

}
