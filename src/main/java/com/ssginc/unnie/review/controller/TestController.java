package com.ssginc.unnie.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test")
    public String layoutPage() {
        return "/layout";  // templates/layout.html을 반환
    }

    @GetMapping("/gpt")
    public String summarize(){
        return "gpt";
    }
}
