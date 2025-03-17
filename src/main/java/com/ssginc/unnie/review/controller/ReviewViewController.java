package com.ssginc.unnie.review.controller;

import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/review")
public class ReviewViewController {

    //영수증 인증
    @GetMapping("/ocr")
    public String reviewOCR(Model model) {

        model.addAttribute("activePage", "map");
        return "/review/reviewOCR";
    }

    //리뷰 작성
    @GetMapping("/create")
    public String reviewCreate(Model model) {
        model.addAttribute("activePage", "map");
        return "/review/reviewCreate";
    }


    //업체 리뷰 목록 조회
    @GetMapping("shop")
    public String reviewShop(Model model) {
        model.addAttribute("activePage", "map");
        return "/review/reviewShop";
    }
}

