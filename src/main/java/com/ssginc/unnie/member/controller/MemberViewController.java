package com.ssginc.unnie.member.controller;

import com.ssginc.unnie.member.service.RegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

   @GetMapping("/checkEmail")
   @ResponseBody
   public ResponseEntity<Boolean> checkMemberEmail(String email) {
       // count가 0이면 사용 가능한 이메일
       int count = registerService.checkMemberEmail(email);
       boolean isUsable = (count == 0);
       return ResponseEntity.ok(isUsable);
   }

    @GetMapping("/checkNickname")
    @ResponseBody
    public ResponseEntity<Boolean> checkMemberNickname(String nickname) {
        int count = registerService.checkMemberNickname(nickname);
        boolean isUsable = (count == 0);
        return ResponseEntity.ok(isUsable);
    }

    /**
     * 로그인 폼
     */
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }



}
