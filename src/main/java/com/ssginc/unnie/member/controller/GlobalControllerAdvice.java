package com.ssginc.unnie.member.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.mypage.dto.member.MyPageMemberResponse;
import com.ssginc.unnie.mypage.service.MyPageMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final MyPageMemberService myPageMemberService;

    /**
     * 레이아웃 프로필이미지
     */
    @ModelAttribute("memberProfile")
    public String addMemberProfile(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        if (memberPrincipal == null) {
            return null;
        }
        Long memberId = memberPrincipal.getMemberId();
        MyPageMemberResponse memberResponse = myPageMemberService.findById(memberId);
        return memberResponse.getMemberProfile();
    }
}
