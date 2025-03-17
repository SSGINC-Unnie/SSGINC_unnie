package com.ssginc.unnie.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssginc.unnie.member.dto.MemberLoginRequest;
import com.ssginc.unnie.member.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    public void loginSuccessTest() throws Exception {
        // 테스트 데이터 생성
        MemberLoginRequest loginRequest = new MemberLoginRequest("test@example.com","test123*");

        // 예상되는 토큰 데이터
        Map<String, String> tokens = Map.of(
                "accessToken", "access-token",
                "refreshToken", "refresh-token"
        );

        // AuthService.login() 설정
        when(authService.login(any(MemberLoginRequest.class), any())).thenReturn(tokens);

        // API 호출 및 응답 검증
        mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().value("refreshToken", "refresh-token"));
    }
}
