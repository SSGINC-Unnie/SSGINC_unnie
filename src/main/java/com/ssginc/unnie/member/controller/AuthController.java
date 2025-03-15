package com.ssginc.unnie.member.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.exception.UnnieJwtException;
import com.ssginc.unnie.common.redis.RedisTokenService;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.JwtUtil;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.member.dto.MemberLoginRequest;
import com.ssginc.unnie.member.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 로그인, Access Token 재발급 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("api/member")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RedisTokenService redisTokenService;

    /**
     * 로그인 API (JWT 발급) - JWT Access Token 및 Refresh Token 발급
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<Map<String,String>>> login(@RequestBody MemberLoginRequest memberLoginRequest, HttpServletResponse response ) {
        // 로그인 실행
        Map<String, String> tokens = authService.login(memberLoginRequest, response);

        // Refresh Token을 HttpOnly 쿠키에 저장
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 6); // 6시간 설정

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "로그인에 성공했습니다.", Map.of(
                "accessToken", tokens.get("accessToken")))); //JWT AccessToken 값 반환
        // "accessToken" : 응답 JSON에서 사용하는 키 값
    }

    /**
     * RefreshToken을 이용해 AccessToken 재발급 API
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto<Map<String, String>>> refreshAccessToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        // RefreshToken을 쿠키에서 가져옴
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null) {
            throw new UnnieJwtException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        // 새로운 AccessToken 발급
        Map<String, String> tokens = authService.refreshAccessToken(refreshToken, response);

        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "AccessToken 재발급 성공", tokens));
    }
}