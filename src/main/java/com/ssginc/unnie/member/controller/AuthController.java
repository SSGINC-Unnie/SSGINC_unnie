package com.ssginc.unnie.member.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.member.dto.MemberLoginRequest;
import com.ssginc.unnie.member.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AuthController는 로그인 및 Access Token 재발급 API를 제공
 */
@Slf4j
@RestController
@RequestMapping("api/member")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 API (JWT 발급) - JWT Access Token 및 Refresh Token 발급
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<Map<String,String>>> login(@RequestBody MemberLoginRequest memberLoginRequest, HttpServletResponse response) {

        Map<String, String> tokens = authService.login(memberLoginRequest, response);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "로그인에 성공했습니다.", tokens));
    }

    /**
     * RefreshToken을 이용해 새로운 AccessToken을 발급합니다.
     * @param refreshTokenCookie 브라우저 쿠키에서 추출한 refreshToken
     * @param response 쿠키 재저장을 위한 HttpServletResponse
     * @return JSON 형태로 새 AccessToken 반환
     */
    @PostMapping("/refresh")
    public  ResponseEntity<ResponseDto<Map<String, String>>> refreshAccessToken(
            @CookieValue(value = "refreshToken", required = false) String refreshTokenCookie,
            HttpServletResponse response) {

        Map<String, String> tokens = authService.refreshAccessToken(refreshTokenCookie, response);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "AccessToken 재발급 성공", tokens));
    }
}
