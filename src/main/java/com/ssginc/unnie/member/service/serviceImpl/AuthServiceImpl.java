package com.ssginc.unnie.member.service.serviceImpl;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.exception.UnnieJwtException;
import com.ssginc.unnie.common.exception.UnnieLoginException;
import com.ssginc.unnie.common.redis.RedisToken;
import com.ssginc.unnie.common.redis.RedisTokenService;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.JwtUtil;
import com.ssginc.unnie.member.dto.MemberLoginRequest;
import com.ssginc.unnie.member.mapper.MemberMapper;
import com.ssginc.unnie.member.service.AuthService;
import com.ssginc.unnie.member.vo.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 로그인, AccessToken 재발급, 로그아웃 인터페이스의 구현 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MemberMapper memberMapper;
    // Redis에 refresh token 저장, 조회, 삭제
    private final RedisTokenService redisTokenService;

    /**
     * 로그인 처리 메서드
     */
    @Override
    public Map<String, String> login(MemberLoginRequest memberLoginRequest, HttpServletResponse response) {
        try {
            // 사용자 인증 (이메일, 비밀번호)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(memberLoginRequest.getMemberEmail(), memberLoginRequest.getMemberPw())
            );

            log.info("인증 성공 - principal: {}", authentication.getPrincipal());

            // 인증 성공 시, principal로 MemberPrincipal 객체를 통해 회원 정보 가져옴
            MemberPrincipal memberPrincipal = (MemberPrincipal) authentication.getPrincipal();
            log.info("인증 성공 - principal: {}", memberPrincipal);

            if (memberPrincipal == null) {
                throw new UnnieLoginException(ErrorCode.MEMBER_NOT_FOUND);
            }

            //회원정보 추출(회원번호, 역할, 닉네임)
            Long memberId = memberPrincipal.getMemberId();
            String role = memberPrincipal.getAuthorities().isEmpty() ? "ROLE_USER"
                    : memberPrincipal.getAuthorities().iterator().next().getAuthority();
            String nickname = memberPrincipal.getMemberNickname();
            log.info("memberId: {}, role: {}, nickname: {}", memberId, role, nickname);

            // Access Token 생성
            String accessToken = jwtUtil.generateToken(memberId, role, nickname);

            // Refresh Token 생성
            String refreshToken = jwtUtil.generateRefreshToken(memberId);

            // Access Token 쿠키 저장
            setCookie(response, "accessToken", accessToken, 3600); // 1시간

            //refresh token Redis에 저장 (memberId를 key로 사용)
            redisTokenService.saveRefreshToken(String.valueOf(memberId), refreshToken);

            log.info("발급된 accessToken: {}", accessToken);
            log.info("발급된 refreshToken: {}", refreshToken);

            return Map.of(
                    "memberId", String.valueOf(memberId),
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            );
        } catch (Exception e) {
            throw new UnnieLoginException(ErrorCode.LOGIN_FAILED, e);
        }
    }

    /**
     * Access token 재발급 메서드
     */
    @Override
    public Map<String, String> refreshAccessToken(Long memberId, HttpServletResponse response) {

        // Redis에서 해당 회원의 refresh token 조회
        RedisToken storedToken = redisTokenService.getRefreshToken(String.valueOf(memberId));
        if (storedToken == null) {
            throw new UnnieJwtException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String storedRefreshToken = storedToken.getRefreshToken();

        // refresh token의 JWT 유효성 검사
        if (!jwtUtil.validateToken(storedRefreshToken)) {
            throw new UnnieJwtException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        //DB 재조회: 현재 회원번호로 Member 엔티티를 가져와, role, nickname 등 최신 정보 확인
        Member member = memberMapper.selectMemberById(memberId);
        if (member == null) {
            throw new UnnieJwtException(ErrorCode.MEMBER_NOT_FOUND);
        }

        //새 Access Token 생성
        String newAccessToken = jwtUtil.generateToken(memberId, member.getMemberRole(), member.getMemberNickname());

        // 쿠키에 새 Access Token 저장
        setCookie(response, "accessToken", newAccessToken, 3600); // 1시간

        log.info("새로운 AccessToken 발급: {}", newAccessToken);

        return Map.of("accessToken", newAccessToken);
    }

    /**
     * 로그아웃: access token 쿠키 제거 및 Redis에 저장된 refresh token 삭제
     */
    @Override
    public void logout(Long memberId, HttpServletResponse response) {
        // 쿠키에서 access token 제거 (유효시간 0으로 설정하여 제거)
        setCookie(response, "accessToken", "", 0);
        // Redis에서 refresh token 삭제
        redisTokenService.deleteRefreshToken(String.valueOf(memberId));
        log.info("회원 {} 로그아웃 처리 완료", memberId);
    }

    /**
     * 쿠키 설정 메서드
     */
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * OAuth 로그인, 회원가입 후 토큰 생성, 저장 메서드
     */
    @Override
    public Map<String, String> oauthToken(HttpServletResponse response, Long memberId, String role, String nickname) {
        //jwt 토큰 생성
        String accessToken = jwtUtil.generateToken(memberId, role, nickname);
        String refreshToken = jwtUtil.generateRefreshToken(memberId);

        //access token 쿠키에 저장
        setCookie(response, "accessToken", accessToken, 3600); // 1시간

        // refresh token은 Redis에 저장
        redisTokenService.saveRefreshToken(String.valueOf(memberId), refreshToken);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }
}
