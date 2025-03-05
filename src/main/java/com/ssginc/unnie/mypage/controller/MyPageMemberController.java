package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.member.MyPageMemberUpdateRequest;
import com.ssginc.unnie.mypage.service.MyPageMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 마이페이지 회원정보 수정 및 탈퇴 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageMemberController {

    private final MyPageMemberService myPageMemberService;

    /**
     * 회원정보 수정
     */
    @PutMapping("/member")
    public ResponseEntity<ResponseDto<Map<String, String>>> updateMember(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestBody MyPageMemberUpdateRequest updateRequest) {

        Long memberId = memberPrincipal.getMemberId();
        int result =  myPageMemberService.updateMember(updateRequest, memberId);
        return ResponseEntity.ok(new ResponseDto<>(
                HttpStatus.OK.value(),"회원 정보 수정이 완료되었습니다.", Map.of("result", String.valueOf(result)))
        );
    }

    /**
     * 회원 탈퇴
     */
    @PatchMapping("/member/withdraw")
    public ResponseEntity<ResponseDto<Map<String, String>>> withdrawMember(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal){

        Long memberId = memberPrincipal.getMemberId();
        int result = myPageMemberService.withdrawMember(memberId);
        return ResponseEntity.ok(new ResponseDto<>(
                HttpStatus.OK.value(),"회원탈퇴가 완료되었습니다.", Map.of("result", String.valueOf(result)))
        );
    }
}
