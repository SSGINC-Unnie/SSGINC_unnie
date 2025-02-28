package com.ssginc.unnie.member.controller;

import com.ssginc.unnie.common.util.JwtUtil;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.member.dto.MemberLoginRequest;
import com.ssginc.unnie.member.service.AuthService;
import com.ssginc.unnie.member.service.OAuthService;
import com.ssginc.unnie.member.vo.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.Map;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("api/oauth")
public class OAuthController {

    private final OAuthService oAuthService;
    private final AuthController authController;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;


    @GetMapping("/login")
    public String loginPage(){
        return "member/login";
    }

    @GetMapping("/emailCheck/{email}")
    @ResponseBody
    public boolean emailCheck(@PathVariable String email, Model model, HttpServletResponse response) {
        boolean result = false;
        Member member = oAuthService.selectMemberByEmail(email);
        if(member != null) {
            result = true;
            //인증 후 token 발급
            Map<String, String> mapResult = authController.oauthToken(response, member.getMemberId(), member.getMemberRole(), member.getMemberNickname());
        }
        return result;
    }


    @PostMapping("/register")
    public String register(@ModelAttribute  Member newMember, Model model) {
        Member registerCheck = oAuthService.selectMemberByEmail(newMember.getMemberEmail());
        if (registerCheck == null) {
            model.addAttribute("naverDto", newMember);
            return "member/register";  // templates/member/register.html
        } else {
            return "redirect:/";
        }
    }


    // 회원가입 폼 제출 후 최종 회원가입 완료 처리
    @PostMapping("/register/complete")
    public String registerComplete(@ModelAttribute Member newMember, Model model, HttpServletResponse response) {
        Member existing  = oAuthService.selectMemberByEmail(newMember.getMemberEmail());
        if (existing  == null) {
            // 신규 회원 등록
            Member member = Member.builder()
                    .memberEmail(newMember.getMemberEmail())
                    .memberPw(encoder.encode(newMember.getMemberPw()))
                    .memberName(newMember.getMemberName())
                    .memberNickname(newMember.getMemberNickname())
                    .memberGender(newMember.getMemberGender())
                    .memberPhone(newMember.getMemberPhone())
                    .memberRole("ROLE_USER")
                    .build();

            //회원 등록
            oAuthService.insertOAuthMember(member);


            // DB에 insert한 후, 같은 이메일로 회원 정보를 조회해서 memberId 가져옴
            Member insertedMember = oAuthService.selectMemberByEmail(member.getMemberEmail());

            //토큰 생성
            String accesstoken = jwtUtil.generateToken(insertedMember.getMemberId(), "ROLE_USER", insertedMember.getMemberNickname());
            String refreshToken = jwtUtil.generateRefreshToken(insertedMember.getMemberId());

            //쿠키에 저장
            Cookie cookie = new Cookie("accessToken", accesstoken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            response.addCookie(cookie);

            Cookie cookie2 = new Cookie("refreshToken", refreshToken);
            cookie2.setHttpOnly(true);
            cookie2.setSecure(true);
            cookie2.setPath("/");
            cookie2.setMaxAge(21600);
            response.addCookie(cookie2);

            // 최종 회원가입 완료 후 홈으로 리다이렉트 (이미 로그인된 상태)
            return "redirect:/";
        } else {
            return "redirect:/";
        }
    }
}
