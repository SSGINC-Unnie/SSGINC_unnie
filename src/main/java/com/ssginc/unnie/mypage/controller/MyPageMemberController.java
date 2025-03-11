package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.exception.UnnieMemberException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.member.MyPageNicknameUpdateRequest;
import com.ssginc.unnie.mypage.dto.member.MyPagePhoneUpdateRequest;
import com.ssginc.unnie.mypage.dto.member.MyPageProfileImgUpdateRequest;
import com.ssginc.unnie.mypage.dto.member.MyPagePwUpdateRequest;
import com.ssginc.unnie.mypage.service.MyPageMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * 프로필 사진 수정
     */
    @PutMapping("/member/profileImg")
    public ResponseEntity<ResponseDto<Map<String, String>>> updateProfile(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal, @RequestPart MyPageProfileImgUpdateRequest profileImgUpdateRequest,
            @RequestPart(value = "memberProfileFile", required = false)  MultipartFile file) {
        int result = myPageMemberService.updateProfile(profileImgUpdateRequest,file);
        return ResponseEntity.ok(new ResponseDto<>(
                HttpStatus.OK.value(),"프로필 수정이 완료되었습니다.", Map.of("result", String.valueOf(result))
        ));
    }

    /**
     * 닉네임 수정
     */
    @PutMapping("/member/nickname")
    public ResponseEntity<ResponseDto<Map<String, String>>> updateNickname(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal, @RequestBody MyPageNicknameUpdateRequest nicknameUpdateRequest) {

        nicknameUpdateRequest.setMemberId(memberPrincipal.getMemberId());
        int result = myPageMemberService.updateNickname(nicknameUpdateRequest);
        return ResponseEntity.ok(new ResponseDto<>(
               HttpStatus.OK.value(),"닉네임 수정이 완료되었습니다.", Map.of("result", String.valueOf(result))
        ));
    }

    /**
     * 전화번호 수정
     */
    @PutMapping("/member/phone")
    public ResponseEntity<ResponseDto<Map<String, String>>> updatePhone(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal, @RequestBody MyPagePhoneUpdateRequest phoneUpdateRequest) {

        phoneUpdateRequest.setMemberId(memberPrincipal.getMemberId());
        int result = myPageMemberService.updatePhone(phoneUpdateRequest);
        return ResponseEntity.ok(new ResponseDto<>(
                HttpStatus.OK.value(), "전화번호 수정이 완료되었습니다.", Map.of("result", String.valueOf(result))
        ));
    }

    /**
     * 비밀번호 수정
     */
    @PutMapping("/member/password")
    public ResponseEntity<ResponseDto<Map<String, String>>> updatePassword(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal, @RequestBody MyPagePwUpdateRequest pwUpdateRequest) {

        pwUpdateRequest.setMemberId(memberPrincipal.getMemberId());
        int result = myPageMemberService.updatePassword(pwUpdateRequest);
        return ResponseEntity.ok(new ResponseDto<>(
                HttpStatus.OK.value(), "비밀번호 수정이 완료되었습니다.", Map.of("result", String.valueOf(result))
        ));
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
