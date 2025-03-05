package com.ssginc.unnie.review.reviewViewController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/review")
public class ReviewOCRViewController {

    @GetMapping("/ocr")
    public String review() {

        return "/review/reviewOCR";
    }
}

