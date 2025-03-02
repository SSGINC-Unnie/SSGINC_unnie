package com.ssginc.unnie.member.service;

import com.ssginc.unnie.member.dto.MemberLoginRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * 회원 본인인증 및 아이디/비밀번호 찾기 기능 관련 인터페이스
 * 이메일 본인인증
 * 전화번호 본인인증
 */
public interface VerificationService {

    //인증번호 발급 및 이메일로 전송
    String sendEmail(String email);

    //입력된 이메일 인증번호 검증
    boolean verifyEmailCode(String email, String code);

    //인증번호 발급 및 전화번호로 인증번호 전송
    String sendPhone(String phone);

    //입력된 전화번호 인증번호 검증
    boolean verifyPhoneCode(String phone, String code);

    // 인증 여부 확인 메서드
    boolean isVerified(String key);

    // 아이디 찾기
    String findId(String memberName, String memberPhone);
    // 비밀번호 찾기
    void findPassword(String memberEmail);

}

