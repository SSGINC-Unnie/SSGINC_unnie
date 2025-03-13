package com.ssginc.unnie.review;

import com.ssginc.unnie.common.util.JwtUtil;
import com.ssginc.unnie.review.controller.ReviewController;
import com.ssginc.unnie.review.service.ReviewService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class) // 실제 컨트롤러 클래스명으로 변경
public class ReviewSummaryTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void testGenerateSummary_Success() throws Exception {
        long shopId = 1;
        // reviewService가 shopId에 대해 요약된 리뷰를 반환하도록 설정
        given(reviewService.summarizeAndSave(shopId)).willReturn("요약된 리뷰 내용");

        mockMvc.perform(post("/api/review/summary")
                        .with(csrf())
                        .with(user("testUser").password("test").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shopId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("리뷰 요약 성공"))
                .andExpect(jsonPath("$.data.reviewSummary").value("요약된 리뷰 내용"));

    }
}