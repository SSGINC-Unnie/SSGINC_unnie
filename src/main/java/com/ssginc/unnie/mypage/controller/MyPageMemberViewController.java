package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.mypage.dto.member.MyPageMemberResponse;
import com.ssginc.unnie.mypage.service.MyPageMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * 마이페이지 회원정보수정, 회원탈퇴 view 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageMemberViewController {

    private final MyPageMemberService myPageMemberService;

    //마이페이지 컨트롤러
    @GetMapping("")
    public String mypage(Model model, @AuthenticationPrincipal MemberPrincipal memberPrincipal){
        MyPageMemberResponse memberInfo = myPageMemberService.findById(memberPrincipal.getMemberId());
        model.addAttribute("member", memberInfo);
        model.addAttribute("activePage", "mypage");
        return "mypage/mypage";
    }

    // 회원정보 수정 페이지
    @GetMapping("/member/edit")
    public String showUpdateMemberInfo(Model model, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        // 회원 정보를 조회한 후 model에 추가
        MyPageMemberResponse memberInfo = myPageMemberService.findById(memberPrincipal.getMemberId());
        model.addAttribute("member", memberInfo);
        model.addAttribute("activePage", "mypage");
        return "mypage/member/updateMemberInfo";
    }
}
