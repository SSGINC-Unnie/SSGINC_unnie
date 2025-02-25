package com.ssginc.unnie.common.util;

import com.ssginc.unnie.common.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 *  JWT 토큰의 생성, 파싱, 유효성 검증 기능을 제공하는 유틸리티 클래스.
 *  JwtConfig에 정의된 SECRET_KEY와 EXPIRATION_TIME 값을 사용하여,
 *  JWT 토큰 생성 시 회원번호(memberId)를 토큰의 subject에, 역할(role), 닉네임(nickname)을 클레임에 추가
 */
@Component
public class JwtUtil {

    // JWT 서명에 사용할 Key 객체
    private Key key;
    // JWT 토큰의 만료 시간
    private long expiration;

    /**
     * JwtUtil 생성자.
     * JwtConfig에 설정된 SECRET_KEY와 EXPIRATION_TIME을 사용해 Key와 만료시간을 초기화합니다.
     */
    public JwtUtil() {
        refreshKey();
    }

    /**
     * JwtConfig에 저장된 SECRET_KEY와 EXPIRATION_TIME을 이용하여 키와 만료시간을 갱신.
     */
    private void refreshKey() {
        this.key = Keys.hmacShaKeyFor(JwtConfig.SECRET_KEY.getBytes());
        this.expiration = JwtConfig.EXPIRATION_TIME;
    }

    /**
     * 회원번호(memberId)와 역할(role), 닉네임(nickname)을 포함하는 JWT 토큰을 생성합니다.
     * @param memberId 회원의 고유 번호 (기본키)
     * @param role 회원의 역할 (예: "ROLE_ADMIN", "ROLE_USER", "ROLE_MANAGER")
     * @param nickname 회원의 닉네임
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(Long memberId, String role, String nickname) {
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))  // 회원번호를 문자열로 변환하여 토큰의 subject로 설정
                .addClaims(Map.of("role",role, // 역할 정보를 "role" 클레임에 추가
                        "nickname", nickname))    // 닉네임 추가
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 현재 시간 + 만료시간을 토큰 만료일로 설정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 발급
     */
    public String generateRefreshToken(Long memberId) {
        // 7일 = 7 * 24시간 * 60분 * 60초 * 1000ms
        long refreshExpiration = 7 * 24 * 60 * 60 * 1000L;

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))      // 회원번호를 subject로
                .claim("type", "refresh")            // Refresh Token을 구분짓는 claim (선택)
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰에서 회원번호(memberId)를 추출합니다.
     *
     * @param token 파싱할 JWT 토큰 문자열
     * @return 추출된 회원번호 (Long 타입)
     */
    public Long getMemberIdFromToken(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                // JWT 토큰을 파싱하여 Claims 추출
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.valueOf(subject);
    }

    /**
     * JWT 토큰에서 회원의 역할(role) 정보를 추출합니다.
     *
     * @param token 파싱할 JWT 토큰 문자열
     * @return 추출된 역할 정보 (문자열)
     */
    public String getRoleFromToken(String token) {
        String role = (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
        return role;
    }

    /**
     * JWT 토큰에서 회원의 닉네임(nickname) 정보를 추출합니다.
     * @param token 파싱할 JWT 토큰 문자열
     * @return 추출된 닉네임
     */
    public String getNicknameFromToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("nickname");
    }

    /**
     * JWT 토큰의 유효성을 검사
     * @param token 검사할 JWT 토큰 문자열
     * @return 토큰이 유효하면 true, 유효하지 않으면 false
     */
    //토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;  // 검증 성공
        } catch (Exception e) {
            return false; // 검증 실패
        }
    }
}
