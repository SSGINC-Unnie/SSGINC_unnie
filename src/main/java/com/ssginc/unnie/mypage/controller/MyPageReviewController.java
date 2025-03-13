package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.common.util.SimpleResponseDto;
import com.ssginc.unnie.mypage.dto.review.MyPageReviewResponse;
import com.ssginc.unnie.mypage.service.MyPageReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 마이페이지 나의 리뷰 관련 컨트롤러 클래스
 */
@Slf4j
@RestController
@RequestMapping("/api/mypage/review")
@RequiredArgsConstructor
public class MyPageReviewController {

    private final MyPageReviewService myPageReviewService;

    @GetMapping("/reviews")
    public ResponseDto<Map<String, List<MyPageReviewResponse>>> getReviewListById(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestParam(name = "sortType", defaultValue = "newest") String sortType,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit", defaultValue = "10") int limit) {

        long reviewMemberId = memberPrincipal.getMemberId();

        List<MyPageReviewResponse> reviews = myPageReviewService.getReviewListById(reviewMemberId, sortType, offset, limit);
        return new ResponseDto<>(HttpStatus.OK.value(), "내가 작성한 리뷰 조회 성공", Map.of("reviews", reviews));
    }


    /**
     * 작성된 리뷰 개수 조회
     * @param memberPrincipal
     * @return
     */
    @GetMapping("/count")
    public ResponseDto<Map<String, Integer>> getReviewCountById(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        if (memberPrincipal == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        long reviewMemberId = memberPrincipal.getMemberId();
        int reviewCount = myPageReviewService.getReviewCountById(reviewMemberId);
        return new ResponseDto<>(HttpStatus.OK.value(), "작성한 리뷰 개수 조회 성공", Map.of("reviewCount", reviewCount));
    }


}
