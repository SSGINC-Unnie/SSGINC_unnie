package com.ssginc.unnie.admin.controller;

import com.ssginc.unnie.admin.service.AdminMemberService;
import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.mypage.dto.member.MyPageMemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminMemberViewController {

    private final AdminMemberService adminMemberService;

    @GetMapping("/allMember")
    public String getAllMembers(Model model){
        model.addAttribute("activePage", "member");
        return "admin/member/getAllMember";
    }
}
