package com.ssginc.unnie.member;

import com.ssginc.unnie.common.redis.RedisTokenService;
import com.ssginc.unnie.common.util.JwtUtil;
import com.ssginc.unnie.member.mapper.MemberMapper;
import com.ssginc.unnie.member.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MemberMapper memberMapper;

    @MockBean
    private RedisTokenService redisTokenService;

    @MockBean
    private HttpServletResponse response;


    @Test
    public void testRefreshAccessToken_InvalidRefreshToken() {
        // 테스트 데이터: 잘못된 RefreshToken
        String invalidRefreshToken = "invalid-refresh-token";

        // jwtUtil.validateToken()에서 false를 반환하도록 설정
        Mockito.when(jwtUtil.validateToken(invalidRefreshToken))
                .thenReturn(false);

        // 잘못된 토큰으로 refreshAccessToken 호출 시 예외 발생 여부 검증
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.refreshAccessToken(invalidRefreshToken, response);
        });

        // 예외 메시지에 예상 메시지의 일부가 포함되어 있는지 확인
        String expectedMessage = "INVALID_REFRESH_TOKEN";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}