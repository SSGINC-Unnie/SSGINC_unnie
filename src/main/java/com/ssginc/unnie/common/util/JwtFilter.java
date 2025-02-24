package com.ssginc.unnie.common.util;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.member.vo.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 요청마다 한 번씩 실행되는 JWT 인증 필터입니다.
 *
 * 이 필터는 HTTP 요청의 쿠키에서 JWT의 "accessToken"을 추출하여 유효성을 검사하고,
 * 토큰이 유효하면 JWT에서 회원번호와 역할 정보를 추출하여 Spring Security의 Authentication 객체를 생성,
 * 이를 SecurityContext에 저장하여 인증된 사용자로 설정
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //SecurityContext에 이미 인증 정보가 있는 경우 JWT 검증 생략
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // 요청 쿠키에서 "accessToken" 쿠키 값을 추출
            Optional<String> tokenOptional = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                    .filter(cookie -> "accessToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst();

            if (tokenOptional.isPresent()) {
                String token = tokenOptional.get();

                // JWT 토큰 유효성 검사
                if (jwtUtil.validateToken(token)) {
                    // JWT에서 회원번호와 역할(Role) 추출
                    Long memberId = jwtUtil.getMemberIdFromToken(token);
                    String role = jwtUtil.getRoleFromToken(token);
                    String nickname = jwtUtil.getNicknameFromToken(token);   // 토큰에 포함된 닉네임

                    // 토큰 정보를 이용하여 임시 Member 객체 생성
                    Member member = new Member();
                    member.setMemberId(memberId);
                    member.setMemberNickname(nickname);
                    member.setMemberRole(role);

                    // 커스텀 MemberPrincipal 객체 생성 (내부에서 권한 정보 반환)
                    MemberPrincipal memberPrincipal = new MemberPrincipal(member);

                    // Authentication 객체 생성 후 SecurityContextHolder에 저장
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(memberPrincipal, null, memberPrincipal.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}