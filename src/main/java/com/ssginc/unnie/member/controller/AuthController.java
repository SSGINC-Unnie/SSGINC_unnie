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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 로그인, Access Token 재발급, 로그아웃 API 처리하는 컨트롤러
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

        Map<String, String> tokens = authService.login(memberLoginRequest, response);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "로그인에 성공했습니다.", tokens));
    }

    /**
     * RefreshToken을 이용해 AccessToken 재발급 API
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto<Map<String, String>>> refreshAccessToken(
            @RequestBody Map<String, String> requestBody,
            HttpServletResponse response) {
        String memberIdStr = requestBody.get("memberId");
        if (memberIdStr == null) {
            throw new UnnieJwtException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Long memberId = Long.valueOf(memberIdStr);
        Map<String, String> tokens = authService.refreshAccessToken(memberId, response);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "AccessToken 재발급 성공", tokens));
    }

    /**
     * 로그아웃 API (JWT 및 Redis refresh token 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<Map<String, String>>> logout(
            @RequestBody Map<String, String> requestBody,
            HttpServletResponse response) {
        String memberIdStr = requestBody.get("memberId");
        if (memberIdStr == null) {
            throw new UnnieJwtException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Long memberId = Long.valueOf(memberIdStr);
        authService.logout(memberId, response);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "로그아웃 성공", Map.of("message", "로그아웃 성공!")));
    }
}
