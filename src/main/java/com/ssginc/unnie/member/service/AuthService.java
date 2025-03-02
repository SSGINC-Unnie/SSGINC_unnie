package com.ssginc.unnie.member.service;

import com.ssginc.unnie.member.dto.MemberLoginRequest;
import com.ssginc.unnie.member.dto.MemberRegisterRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * 로그인, AccessToken 재발급, 로그아웃 기능 인터페이스
 */
public interface AuthService {

    //로그인
    Map<String, String> login(MemberLoginRequest memberLoginRequest, HttpServletResponse response);
    //AccessToken 재발급
    Map<String, String> refreshAccessToken(Long memberId, HttpServletResponse response);
    //로그아웃
    void logout(Long memberId, HttpServletResponse response);
}
