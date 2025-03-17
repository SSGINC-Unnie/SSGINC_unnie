package com.ssginc.unnie.mypage.controller;

import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/mypage")
public class MypageReviewViewController {

    //나의 리뷰 조회
    @GetMapping("/review")
    public String reviewMy(Model model) {
        model.addAttribute("activePage", "mypage");
        return "/mypage/review/myReview";
    }
}