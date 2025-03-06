package com.ssginc.unnie.review.reviewViewController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/review")
public class ReviewViewController {

    @GetMapping("/ocr")
    public String reviewOCR() {

        return "/review/reviewOCR";
    }

    @GetMapping("/create")
    public String reviewCreate() {

        return "/review/reviewCreate";
    }
}

