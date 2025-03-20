package com.ssginc.unnie.common.config;

import com.ssginc.unnie.common.util.JwtFilter;
import com.ssginc.unnie.member.service.serviceImpl.MemberDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig 클래스는 Spring Security의 전체 보안 설정을 구성.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberDetailsServiceImpl memberDetailsService;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        //리뷰 작성 - 로그인한 사용자만 접근
                        .requestMatchers("/review/**").authenticated()
                        //마이페이지 관련 - 로그인한 사용자만 접근
                        .requestMatchers("/mypage/**").authenticated()
                        //관리자 페이지 - ADMIN 권한 필요
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 마이페이지 (업체 관리) - MANAGER 권한 필요
                        .requestMatchers("/mypage/myshop/**").hasAnyRole("MANAGER") //mypage 업체관리 페이지는 업체담당자(MANAGER)만 접근 가능
                        // 그 외 모든 요청은 기본적으로 허용
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable()
                )
                .logout(logout -> logout.disable()
                )

                // 세션 사용 안 함 (JWT 기반 인증은 Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // JwtFilter를 UsernamePasswordAuthenticationFilter 전에 추가
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
