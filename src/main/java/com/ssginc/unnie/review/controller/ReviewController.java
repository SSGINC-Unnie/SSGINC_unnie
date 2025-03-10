package com.ssginc.unnie.review.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.review.dto.ReviewCreateRequest;
import com.ssginc.unnie.review.dto.ReviewGetResponse;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.review.dto.ReviewGuestGetResponse;
import com.ssginc.unnie.review.dto.ReviewUpdateRequest;
import com.ssginc.unnie.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    // ✅ 업로드 디렉토리 (프로젝트 내 static 폴더)
//    private static final String UPLOAD_DIR = "C:/workSpace/SSGINC_Unnie/src/main/resources/static/upload";

    // ✅ 업로드 디렉토리 (프로젝트 내 static 폴더)
    private static final String UPLOAD_DIR = "/Users/wish/IdeaProjects/SSGINC_unnie/src/main/resources/static/upload";

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
        reviewCreateRequest.setKeywordId(parseKeywordIds(keywordId));

        MultipartFile file = reviewCreateRequest.getFile(); // ✅ 'file' 그대로 유지
        String filePath = null;

        if (file != null && !file.isEmpty()) {
            filePath = saveFile(file);
        }

        // DTO에 파일 경로 저장 (MyBatis에서 사용할 필드)
        reviewCreateRequest.setReviewImage(filePath); // ✅ DB에는 파일 경로만 저장

        long reviewId = reviewService.createReview(reviewCreateRequest);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "리뷰 작성에 성공했습니다.", Map.of("reviewId", String.valueOf(reviewId)))
        );

    }

    public String saveFile(MultipartFile file) {
        try {
            // 폴더가 없으면 생성
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일명 생성 (UUID + 원본 파일명)
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);

            // 파일 저장
            file.transferTo(filePath.toFile());

            // ✅ 클라이언트가 접근 가능한 URL 경로 반환
            return "/upload/" + filename;

        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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

}
