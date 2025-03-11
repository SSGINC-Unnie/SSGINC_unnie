package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.member.vo.Member;
import com.ssginc.unnie.mypage.dto.member.MyPageMemberResponse;
import com.ssginc.unnie.mypage.service.MyPageMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    // 회원정보 수정 페이지
    @GetMapping("/member/edit")
    public String showUpdateMemberInfo(Model model, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        // 회원 정보를 조회한 후 model에 추가
        MyPageMemberResponse memberInfo = myPageMemberService.findById(memberPrincipal.getMemberId());
        model.addAttribute("member", memberInfo);
        return "mypage/member/updateMemberInfo";
    }

    //회원 탈퇴 페이지
}
