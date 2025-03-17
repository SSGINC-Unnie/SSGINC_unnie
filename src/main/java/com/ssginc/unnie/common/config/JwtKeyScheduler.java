package com.ssginc.unnie.common.config;

import com.ssginc.unnie.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtKeyScheduler {

    private final JwtUtil jwtUtil;

    // 하루에 한 번 (86400000 밀리초) 키를 갱신
    @Scheduled(fixedRate = 86400000)
    public void updateJwtSecretKey() {
        String newKey = JwtConfig.generateNewKey();
        JwtConfig.setSecretKey(newKey);

        // JwtUtil에 새로운 값 반영
        jwtUtil.refreshKey();

        log.info("JWT secret key 갱신: {}", newKey);
    }
}
