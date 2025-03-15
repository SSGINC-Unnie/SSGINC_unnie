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

            // AccessToken과 RefreshToken 생성
            // Access Token
            String accessToken = jwtUtil.generateToken(memberId, role, nickname);
            // Refresh Token
            String refreshToken = jwtUtil.generateRefreshToken(memberId);

            // Access Token 쿠키 저장
            setCookie(response, "accessToken", accessToken, 3600); // 1시간

            //refresh token Redis에 저장 (memberId를 key로 사용)
            redisTokenService.saveRefreshToken(String.valueOf(memberId), refreshToken);

            log.info("발급된 accessToken: {}", accessToken);
            log.info("발급된 refreshToken: {}", refreshToken);

            // AccessToken과 RefreshToken을 Map으로 반환
            return Map.of(
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
    public Map<String, String> refreshAccessToken(String refreshToken, HttpServletResponse response) {

        // RefreshToken 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new UnnieJwtException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Redis에서 RefreshToken 조회
        Long memberId = jwtUtil.getMemberIdFromToken(refreshToken);
        RedisToken storedToken = redisTokenService.getRefreshToken(String.valueOf(memberId));

        if (storedToken == null || !storedToken.getRefreshToken().equals(refreshToken)) {
            throw new UnnieJwtException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // DB에서 최신 회원 정보 조회 (변경된 role이나 nickname 반영)
        // 새로운 AccessToken 발급
        Member member = memberMapper.selectMemberById(memberId);
        String newAccessToken = jwtUtil.generateToken(memberId, member.getMemberRole(), member.getMemberNickname());

        // 쿠키에 새 Access Token 저장
        setCookie(response, "accessToken", newAccessToken, 3600); // 1시간

        log.info("새로운 AccessToken 발급: {}", newAccessToken);

        return Map.of("accessToken", newAccessToken);
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

        //Refresh Token 쿠키에 저장
        setCookie(response, "refreshToken", refreshToken, 60 * 60 * 6); //6시간

        // refresh token Redis에 저장
        redisTokenService.saveRefreshToken(String.valueOf(memberId), refreshToken);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }
}