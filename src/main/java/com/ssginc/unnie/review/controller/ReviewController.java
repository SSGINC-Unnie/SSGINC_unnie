package com.ssginc.unnie.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewController {
    /**
     * 파일 업로드 테스트 페이지 제공
     */
    @GetMapping("/ocr")
    public String uploadPage() {
        return "ocr";  // templates/upload.html을 반환
    }

    @GetMapping("/gpt")
    public String summarize(){
        return "gpt";
    }
}
