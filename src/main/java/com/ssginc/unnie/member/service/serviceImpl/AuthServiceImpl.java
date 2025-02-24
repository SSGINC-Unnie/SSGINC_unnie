package com.ssginc.unnie.member.service.serviceImpl;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.exception.UnnieJwtException;
import com.ssginc.unnie.common.exception.UnnieLoginException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.JwtUtil;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.member.dto.MemberLoginRequest;
import com.ssginc.unnie.member.mapper.MemberMapper;
import com.ssginc.unnie.member.service.AuthService;
import com.ssginc.unnie.member.vo.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 로그인 및 AccessToken 재발급 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MemberMapper memberMapper;

    @Override
    public Map<String, String> login(MemberLoginRequest memberLoginRequest, HttpServletResponse response) {
        try {
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(memberLoginRequest.getMemberEmail(), memberLoginRequest.getMemberPw())
            );

            log.info("인증 성공 - principal: {}", authentication.getPrincipal());

            // 인증 성공 시, principal로 MemberPrincipal 객체를 얻게 됨
            MemberPrincipal memberPrincipal = (MemberPrincipal) authentication.getPrincipal();
            log.info("인증 성공 - principal: {}", memberPrincipal);

            if (memberPrincipal == null) {
                throw new UnnieLoginException(ErrorCode.MEMBER_NOT_FOUND);
            }

            log.info("MemberPrincipal username: {}", memberPrincipal.getUsername());
            log.info("MemberPrincipal password: {}", memberPrincipal.getPassword());
            log.info("MemberPrincipal authorities: {}", memberPrincipal.getAuthorities());

            // memberPrincipal에서 회원번호, 역할, 닉네임 정보 가져오기
            Long memberId = memberPrincipal.getMemberId();
            String role = memberPrincipal.getAuthorities().isEmpty() ? "ROLE_USER"
                    : memberPrincipal.getAuthorities().iterator().next().getAuthority();
            String nickname = memberPrincipal.getMemberNickname();
            log.info("memberId: {}", memberId);
            log.info("role: {}", role);
            log.info("nickname: {}", nickname);

            // Access Token 생성
            String accessToken = jwtUtil.generateToken(memberId, role, nickname);

            // Refresh Token 생성
            String refreshToken = jwtUtil.generateRefreshToken(memberId);

            // Access Token 쿠키 저장
            setCookie(response, "accessToken", accessToken, 3600); // 1시간

            // Refresh Token 쿠키 저장
            setCookie(response, "refreshToken", refreshToken, 7 * 24 * 60 * 60); // 7일

            log.info("발급된 accessToken: {}", accessToken);
            log.info("발급된 refreshToken: {}", refreshToken);

            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        } catch (Exception e) {
            throw new UnnieLoginException(ErrorCode.LOGIN_FAILED, e);
        }
    }

    @Override
    public Map<String, String> refreshAccessToken(String refreshTokenCookie, HttpServletResponse response) {

        // Refresh 토큰이 쿠키에 있는지 확인
        if (refreshTokenCookie == null) {
            throw new UnnieJwtException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // Refresh 토큰 유효성 검사
        if (!jwtUtil.validateToken(refreshTokenCookie)) {
            throw new UnnieJwtException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Refresh Token이 유효하면 subject(회원번호) 추출
        Long memberId = jwtUtil.getMemberIdFromToken(refreshTokenCookie);

        // DB 재조회: 현재 회원번호로 Member 엔티티를 가져와, role, nickname 등 최신 정보 확인
        Member member = memberMapper.selectMemberById(memberId);
        if (member == null) {
            throw new UnnieJwtException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 새로운 Access Token 발급 (DB에서 가져온 최신 정보 사용)
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
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
