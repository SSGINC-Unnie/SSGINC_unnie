package com.ssginc.unnie.review.controller;

import com.ssginc.unnie.review.service.serviceImpl.OpenAIServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review")
public class OpenAIController {
    private final OpenAIServiceImpl openAIService;

    public OpenAIController(OpenAIServiceImpl openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/summarize")
    public String summarize(@RequestBody String review) {
        return openAIService.summarizeReview(review);
    }
}
