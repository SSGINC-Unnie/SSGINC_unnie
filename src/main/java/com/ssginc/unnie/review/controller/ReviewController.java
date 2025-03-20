package com.ssginc.unnie.review.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.review.dto.*;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //S3에 이미지 업로드 후 경로 저장
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3;
    /**
     * 리뷰 등록
     * JWT 토큰을 통해 인증된 회원의 memberId를 ReviewCreateRequest에 세팅합니다.
     */
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto<Map<String, String>>> createReview(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @ModelAttribute ReviewCreateRequest reviewCreateRequest,
            @RequestParam(value = "keywordId", required = true) String keywordId) {

        if (memberPrincipal == null) {
            System.out.println("MemberPrincipal이 주입되지 않음.");
        } else {
            System.out.println("Authenticated Member ID: " + memberPrincipal.getMemberId());
        }

        // JWT에서 사용자 ID 설정
        reviewCreateRequest.setReviewMemberId(memberPrincipal.getMemberId());

        // String으로 받은 문자열을 List로 변환
        reviewCreateRequest.setKeywordId(parseKeywordIds(keywordId));

        // 이미지 파일 업로드 처리 (S3에 업로드 후 URL 획득)
        MultipartFile file = reviewCreateRequest.getFile();
        String filePath = null;
        if (file != null && !file.isEmpty()) {
            filePath = saveFile(file);
        }
        // DB에는 파일 경로만 저장
        reviewCreateRequest.setReviewImage(filePath);

        long reviewId = reviewService.createReview(reviewCreateRequest);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "리뷰 작성에 성공했습니다.", Map.of("reviewId", String.valueOf(reviewId)))
        );
    }

    public String saveFile(MultipartFile file) {
        // 원본 파일명과 안전한 파일명 생성
        String originalFileName = file.getOriginalFilename();
        String safeFileName = UUID.randomUUID().toString() + "_"
                + originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        try {
            // 파일 메타데이터 설정 (필요한 경우 ContentType 등 추가)
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            // S3에 파일 업로드 (버킷, 파일명, InputStream, 메타데이터, 퍼블릭 읽기 권한)
            amazonS3.putObject(new PutObjectRequest(bucketName, safeFileName, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }

        // S3 URL 획득 및 반환
        String s3Url = amazonS3.getUrl(bucketName, safeFileName).toString();
        System.out.println("Saving file to: " + s3Url);
        return s3Url;
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
            @PathVariable("reviewId") long reviewId) {

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
            @RequestParam(value = "keywordId", required = false) String keywordId) {

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

        reviewUpdateRequest.setKeywordId(parseKeywordIds(keywordId));

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
    private List<Integer> parseKeywordIds(String keywordId) {
        if (keywordId != null && !keywordId.trim().isEmpty()) {
            return Arrays.stream(keywordId.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 업체 리뷰 목록 조회 (회원 전용)
     * @param shopId  업체 ID (경로 변수)
     * @param keyword 필터링할 키워드 (없으면 빈 문자열)
     * @param sortType 정렬 방식 ('newest', 'oldest'; 기본값 'newest')
     * @param offset  페이지네이션 시작 값 (기본값 0)
     * @param limit   조회 건수 (기본값 10)
     * @param memberPrincipal 로그인한 회원 정보 (Spring Security)
     * @return 업체 리뷰 목록을 포함한 응답 DTO
     */
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getReviewListByShop(
            @PathVariable("shopId") long shopId,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "newest") String sortType,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        // 로그인 여부 체크
        if (memberPrincipal == null) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        // 서비스 호출: 업체 리뷰 목록 조회
        List<ReviewGetResponse> reviewList = reviewService.getReviewListByShop(shopId, keyword, sortType, offset, limit);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "업체 리뷰 목록 조회 성공", Map.of("reviews", reviewList))
        );
    }

    /**
     * 업체 리뷰 목록 조회 (비회원 전용)
     * 비회원은 최근 리뷰 3개만 조회하며, 리뷰 내용은 블러 처리된 상태로 전달됩니다.
     * @param shopId  업체 ID (경로 변수)
     * @param keyword 필터링할 키워드 (없으면 빈 문자열)
     * @param sortType 정렬 방식 ('newest', 'oldest'; 기본값 'newest')
     * @param offset  페이지네이션 시작 값 (기본값 0)
     * @param limit   조회 건수 (기본값 3)
     * @return 비회원용 업체 리뷰 목록을 포함한 응답 DTO
     */
    @GetMapping("/guest/shop/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getReviewListByShopGuest(
            @PathVariable("shopId") long shopId,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "newest") String sortType,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "3") int limit) {

        // 인증 정보 없이 접근 가능하므로 별도의 로그인 체크가 필요 없음
        List<ReviewGuestGetResponse> reviewList = reviewService.getReviewListByShopGuest(shopId, keyword, sortType, offset, limit);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "비회원용 업체 리뷰 목록 조회 성공", Map.of("reviews", reviewList))
        );
    }

    /**
     * 업체 리뷰 개수 조회 REST API
     * @param shopId 업체 ID (경로 변수)
     * @param keyword 필터링할 키워드 (없으면 빈 문자열)
     * @return 업체 리뷰 개수를 포함한 응답 DTO
     */
    @GetMapping("/shop/{shopId}/count")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getReviewCount(
            @PathVariable("shopId") long shopId,
            @RequestParam(required = false, defaultValue = "") String keyword) {

        int totalReviewCount = reviewService.getReviewCountByShop(shopId, keyword);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "업체 리뷰 개수 조회 성공", Map.of("totalReviewCount", totalReviewCount))
        );
    }

    /**
     * 리뷰 요약 AI
     * @param reviewSummaryRequest
     * @return 요약된 내용
     */
    @PostMapping("/summary")
    public ResponseEntity<ResponseDto<Map<String, Object>>> generateSummary(@RequestBody ReviewSummaryRequest reviewSummaryRequest) {
        String summary = reviewService.summarizeAndSave(reviewSummaryRequest.getShopId());

        ReviewSummaryResponse reviewSummaryResponse = new ReviewSummaryResponse();

        reviewSummaryResponse.setShopId(reviewSummaryRequest.getShopId());
        reviewSummaryResponse.setReviewSummary(summary);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "리뷰 요약 성공", Map.of("reviewSummary", reviewSummaryResponse.getReviewSummary()))
        );
    }
}
