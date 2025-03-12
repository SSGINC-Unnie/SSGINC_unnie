package com.ssginc.unnie.member.controller;

import com.ssginc.unnie.member.service.RegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 회원가입, 로그인, 아이디/비밀번호 찾기 view 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberViewController {

    private final RegisterService registerService;

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
}
