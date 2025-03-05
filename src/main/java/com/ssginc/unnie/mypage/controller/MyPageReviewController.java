package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.review.MyPageReviewResponse;
import com.ssginc.unnie.mypage.service.MyPageReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseDto<Map<String, List<MyPageReviewResponse>>> getReviewListById (
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        long reviewMemberId = memberPrincipal.getMemberId();
        List<MyPageReviewResponse> reviews = myPageReviewService.getReviewListById(reviewMemberId);
        return new ResponseDto<>(HttpStatus.OK.value(), "내가 작성한 리뷰 조회 성공", Map.of("reviews", reviews));
    }

}
