package com.ssginc.unnie.mypage.controller;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.community.MyPageBoardsResponse;
import com.ssginc.unnie.mypage.dto.community.MyPageCommentsResponse;
import com.ssginc.unnie.mypage.service.MyPageCommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 마이페이지 나의 게시글 / 댓글 관련 컨트롤러 클래스
 */
@Slf4j
@RestController
@RequestMapping("/api/mypage/community")
@RequiredArgsConstructor
public class MyPageCommunityController {

    private final MyPageCommunityService myPageCommunityService;

    /**
     * 나의 게시글 목록 조회
     */
    @GetMapping("/board")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getMyBoards(
            @RequestParam(defaultValue = "1") int page,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal){

        long memberId = memberPrincipal.getMemberId();

        PageInfo<MyPageBoardsResponse> myBoards = myPageCommunityService.getMyBoards(memberId, page);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "나의 게시글 목록 조회 성공", Map.of("boards", myBoards))
        );
    }

    /**
     * 나의 댓글 목록 조회
     */
    @GetMapping("/comment")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getMyComments(
            @RequestParam(defaultValue = "1") int page,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal){

        long memberId = memberPrincipal.getMemberId();

        PageInfo<MyPageCommentsResponse> myComments = myPageCommunityService.getMyComments(memberId, page);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "나의 댓글 목록 조회 성공", Map.of("comments", myComments))
        );
    }
}
