package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.review.MyPageReviewResponse;
import com.ssginc.unnie.mypage.service.MyPageReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 마이페이지 나의 리뷰 관련 컨트롤러 클래스
 */
@Slf4j
@RestController
@RequestMapping("/api/mypage/review")
@RequiredArgsConstructor
public class MyPageReviewController {

    private final MyPageReviewService myPageReviewService;

    @GetMapping("reviews")
    public ResponseEntity<ResponseDto<List<MyPageReviewResponse>>> getReviewListById(
            @RequestParam("reviewMemberId") long reviewMemberId) {
        List<MyPageReviewResponse> reviews = myPageReviewService.getReviewListById(reviewMemberId);
        return ResponseEntity.ok(new ResponseDto<>(200, "내가 작성한 리뷰 조회 성공", reviews));
    }
}
