package com.ssginc.unnie.member.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.redis.RedisTokenService;
import com.ssginc.unnie.member.service.RegisterService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 회원가입, 로그인, 아이디/비밀번호 찾기, 로그아웃 view 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberViewController {

    private final RegisterService registerService;
    private final RedisTokenService redisTokenService;

    /**
     * 회원가입 폼
     */
    @GetMapping("/register")
    public String registerForm() {
        return "member/register";
    }

    /**
     * 로그인 폼
     */
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    /**
     * 아이디/비밀번호 찾기
     */
    @GetMapping("/findMember")
    public String findMember() {
        return "member/findMember";
    }

    /**
     * 로그아웃: Access,Refresh token 쿠키 제거 및 Redis에 저장된 Refresh token 삭제
     */
    @PostMapping("/logout")
    public String logout(@AuthenticationPrincipal MemberPrincipal memberPrincipal, HttpServletResponse response) {

        // Refresh Token Redis에서 삭제
        if (memberPrincipal != null) {
            Long memberId = memberPrincipal.getMemberId();
            redisTokenService.deleteRefreshToken(String.valueOf(memberId));
        }

        // Access Token 쿠키 만료 처리
        Cookie accessTokenCookie = new Cookie("accessToken", "");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0); // 쿠키 만료
        response.addCookie(accessTokenCookie);

        // Refresh Token 쿠키 만료 처리
        Cookie refreshTokenCookie  = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 쿠키 만료
        response.addCookie(refreshTokenCookie);

        // 로그아웃 성공 후 홈 페이지로 리다이렉션
        return "redirect:/";
    }
}