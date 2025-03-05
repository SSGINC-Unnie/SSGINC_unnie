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
     * JWT 토큰을 통해 인증된 회원의 memberId를 ReviewCreateRequest에 세팅합니다.
     */
    @PostMapping("")
    public ResponseEntity<ResponseDto<Map<String, String>>> createReview(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestBody ReviewCreateRequest reviewCreateRequest,
            @RequestParam(value = "keywordIds", required = false) String keywordIdsStr) {

        // 토큰에서 추출한 회원 ID를 DTO에 설정
        reviewCreateRequest.setReviewMemberId(memberPrincipal.getMemberId());
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
    public ResponseEntity<ResponseDto<Map<String, Object>>> getReview(
            @PathVariable("reviewId") long reviewId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        ReviewGetResponse review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "리뷰 조회에 성공했습니다.", Map.of("review", review))
        );
    }

    /**
     * 리뷰 키워드 조회
     */
    @GetMapping("/keywords/{reviewId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getReviewKeywords(
            @PathVariable("reviewId") long reviewId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        List<String> keywords = reviewService.selectReviewKeywordsByReviewId(reviewId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "키워드 조회에 성공했습니다.", Map.of("keywords", keywords))
        );
    }

    /**
     * 리뷰 수정
     * JWT 토큰에서 추출한 memberId를 DTO에 세팅하여, 소유자 확인 후 수정합니다.
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> updateReview(
            @PathVariable("reviewId") long reviewId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestBody ReviewUpdateRequest reviewUpdateRequest,
            @RequestParam(value = "keywordIds", required = false) String keywordIdsStr) {

        // 관리자(ROLE_ADMIN)만 관리자 권한으로 처리하고, 그 외는 반드시 리뷰 작성자와 일치해야 함.
        String role = memberPrincipal.getAuthorities().isEmpty()
                ? "ROLE_USER"
                : memberPrincipal.getAuthorities().iterator().next().getAuthority();

        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            ReviewGetResponse existingReview = reviewService.getReviewById(reviewId);
            if (existingReview.getReviewMemberId() != memberPrincipal.getMemberId()) {
                throw new RuntimeException("리뷰 수정 권한이 없습니다.");
            }
        }

        // 수정 요청 시 토큰의 회원 ID를 DTO에 설정하여 소유자 확인에 사용합니다.
        reviewUpdateRequest.setReviewMemberId(memberPrincipal.getMemberId());
        reviewUpdateRequest.setKeywordIds(parseKeywordIds(keywordIdsStr));
        reviewUpdateRequest.setReviewId(reviewId);
        long updatedReviewId = reviewService.updateReview(reviewUpdateRequest);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "리뷰 수정에 성공했습니다.", Map.of("reviewId", String.valueOf(updatedReviewId)))
        );
    }

    /**
     * 리뷰 소프트 딜리트
     * 사용자와 관리자의 삭제 권한을 토큰 정보를 통해 구분합니다.
     */
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> softDeleteReview(
            @PathVariable("reviewId") long reviewId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        if (memberPrincipal == null) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }
        // 관리자(ROLE_ADMIN)는 별도 소유자 확인 없이 처리, 그 외는 반드시 리뷰 작성자와 일치해야 함.
        String role = memberPrincipal.getAuthorities().isEmpty()
                ? "ROLE_USER"
                : memberPrincipal.getAuthorities().iterator().next().getAuthority();

        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            ReviewGetResponse existingReview = reviewService.getReviewById(reviewId);
            if (existingReview.getReviewMemberId() != memberPrincipal.getMemberId()) {
                throw new RuntimeException("리뷰 삭제 권한이 없습니다.");
            }
        }

        // 1: 사용자, 2: 관리자
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
