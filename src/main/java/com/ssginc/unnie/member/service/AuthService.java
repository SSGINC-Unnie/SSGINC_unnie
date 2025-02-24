package com.ssginc.unnie.member.service;

import com.ssginc.unnie.member.dto.MemberLoginRequest;
import com.ssginc.unnie.member.dto.MemberRegisterRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * 로그인 및 AccessToken 재발급 서비스.
 */
public interface AuthService {

    Map<String, String> login(MemberLoginRequest memberLoginRequest, HttpServletResponse response);

    Map<String, String> refreshAccessToken(String refreshTokenCookie, HttpServletResponse response);
}
