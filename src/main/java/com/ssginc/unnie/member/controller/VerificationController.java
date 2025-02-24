package com.ssginc.unnie.member.controller;


import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.common.util.SimpleResponseDto;
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
        return ResponseEntity.ok(new SimpleResponseDto(HttpStatus.OK.value(), "인증 코드가 발송되었습니다."));
    }

    //이메일 인증번호 검증
    @PostMapping("/checkEmail")
    public ResponseEntity<ResponseDto<Map<String, Boolean>>> verifyEmailCode(@RequestBody Map<String, String> requestData) {
        boolean isVerified = verificationService.verifyEmailCode(requestData.get("memberEmail"), requestData.get("code"));
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "이메일 인증 처리 완료되었습니다.", Map.of("result", isVerified))
        );
    }

    //전화번호 인증번호 발급 및 전송
    @PostMapping("/sendPhone")
    public ResponseEntity<SimpleResponseDto> sendPhone(@RequestBody Map<String, String> requestData) {
        verificationService.sendPhone(requestData.get("memberPhone"));
        return ResponseEntity.ok(new SimpleResponseDto(HttpStatus.OK.value(), "인증 코드가 발송되었습니다."));
    }

    //전화번호 인증번호 검증
    @PostMapping("/checkPhone")
    public ResponseEntity<ResponseDto<Map<String, Boolean>>> verifyPhoneCode(@RequestBody Map<String, String> requestData) {
        boolean isVerified = verificationService.verifyPhoneCode(requestData.get("memberPhone"), requestData.get("code")); // 인증번호 검증
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "전화번호 인증 처리 완료되었습니다.", Map.of("result", isVerified))
        );
    }
}
