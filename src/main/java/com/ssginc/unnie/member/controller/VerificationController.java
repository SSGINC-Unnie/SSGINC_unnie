package com.ssginc.unnie.member.controller;


import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.common.util.SimpleResponseDto;
import com.ssginc.unnie.member.dto.MemberFindIdRequest;
import com.ssginc.unnie.member.dto.MemberFindPwRequest;
import com.ssginc.unnie.member.dto.MemberRegisterRequest;
import com.ssginc.unnie.member.service.RegisterService;
import com.ssginc.unnie.member.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 회원가입 본인인증 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class VerificationController {

    private final VerificationService verificationService;

    //이메일 인증번호 발급 및 전송
    @PostMapping("/sendEmail")
    public ResponseEntity<SimpleResponseDto> sendEmail(@RequestBody Map<String, String> requestData ) {
        verificationService.sendEmail(requestData.get("memberEmail"));
        return ResponseEntity.ok(new SimpleResponseDto(HttpStatus.OK.value(), "인증번호가 발송되었습니다."));
    }

    //이메일 인증번호 검증
    @PostMapping("/checkEmail")
    public ResponseEntity<ResponseDto<Map<String, Boolean>>> verifyEmailCode(@RequestBody Map<String, String> requestData) {
        boolean isVerified = verificationService.verifyEmailCode(requestData.get("memberEmail"), requestData.get("code"));
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "인증 완료되었습니다.", Map.of("result", isVerified))
        );
    }

    //전화번호 인증번호 발급 및 전송
    @PostMapping("/sendPhone")
    public ResponseEntity<SimpleResponseDto> sendPhone(@RequestBody Map<String, String> requestData) {
        verificationService.sendPhone(requestData.get("memberPhone"));
        return ResponseEntity.ok(new SimpleResponseDto(HttpStatus.OK.value(), "인증번호가 발송되었습니다."));
    }

    //전화번호 인증번호 검증
    @PostMapping("/checkPhone")
    public ResponseEntity<ResponseDto<Map<String, Boolean>>> verifyPhoneCode(@RequestBody Map<String, String> requestData) {
        boolean isVerified = verificationService.verifyPhoneCode(requestData.get("memberPhone"), requestData.get("code")); // 인증번호 검증
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "인증 완료되었습니다.", Map.of("result", isVerified))
        );
    }

    //이메일(아이디) 찾기
    @PostMapping("/findId")
    public ResponseEntity<ResponseDto<String>> findId(@RequestBody MemberFindIdRequest request) {
        log.info("[아이디 찾기 요청] 이름: {}, 전화번호: {}", request.getMemberName(), request.getMemberPhone());
        String email = verificationService.findId(request.getMemberName(), request.getMemberPhone());
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "아이디 찾기 성공", email));
    }

    //비밀번호 찾기
    @PostMapping("/findPw")
    public ResponseEntity<SimpleResponseDto> findPassword(@RequestBody MemberFindPwRequest request) {
        log.info("[비밀번호 찾기 요청] 이메일: {}", request.getMemberEmail());
        verificationService.findPassword(request.getMemberEmail());
        return ResponseEntity.ok(new SimpleResponseDto(HttpStatus.OK.value(), "임시 비밀번호가 전송되었습니다." ));
    }
}
