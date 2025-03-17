package com.ssginc.unnie.common.config;

import java.util.Base64;
import java.util.UUID;
/**
 * Jwt 서명에 사용되는 key와 토큰 유효시간을 포함하는 클래스
 */
public class JwtConfig {
    // JWT 서명 키 (Base64 인코딩된 형태로 관리)
    // JWT 서명 키는 스케줄러 사용해서 주기적으로 갱신
    private static String SECRET_KEY = generateNewKey(); // 초기화 시 생성

    // 유효시간: 1시간
    public static final long EXPIRATION_TIME = 3600000L;

    public static synchronized String getSecretKey() {
        return SECRET_KEY;
    }

    public static synchronized void setSecretKey(String newKey) {
        SECRET_KEY = newKey;
    }

    // 새 키를 생성하는 메서드 (UUID를 Base64 인코딩)
    public static String generateNewKey() {
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
    }
}
