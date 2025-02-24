package com.ssginc.unnie.common.config;

/**
 * Jwt 서명에 사용되는 key와 토큰 유효시간을 포함하는 클래스
 */
public class JwtConfig {
    // JWT 서명 키 (Base64 인코딩된 형태로 관리)
    // JWT 서명 키는 스케줄러 사용해서 주기적으로 갱신
    public static String SECRET_KEY = "TXlTZWNyZXRLZXlGb3JKd3RBdXRoZW50aWNhdGlvbjEyMzQ1IQ==";

    // 토큰 유효시간: 1시간
    public static final long EXPIRATION_TIME = 3600000L;
}
