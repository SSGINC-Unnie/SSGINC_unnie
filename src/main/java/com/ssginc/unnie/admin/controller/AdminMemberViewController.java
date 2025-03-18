package com.ssginc.unnie.admin.controller;

import com.ssginc.unnie.admin.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminMemberViewController {

    private final AdminMemberService adminMemberService;

    @GetMapping("/allMember")
    public String getAllMembers(){
        return "admin/member/getAllMember";
    }
}
