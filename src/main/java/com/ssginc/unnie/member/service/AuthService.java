package com.ssginc.unnie.member.service;

import com.ssginc.unnie.member.dto.MemberLoginRequest;
import com.ssginc.unnie.member.dto.MemberRegisterRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * 로그인, AccessToken 재발급 인터페이스
 */
public interface AuthService {

    //로그인
    Map<String, String> login(MemberLoginRequest memberLoginRequest, HttpServletResponse response);
    //AccessToken 재발급
    Map<String, String> refreshAccessToken(String refreshToken, HttpServletResponse response);
    //OAuth 로그인, 회원가입 후 토큰 생성
    Map<String,String> oauthToken(HttpServletResponse response, Long memberId, String role, String nickname);
}