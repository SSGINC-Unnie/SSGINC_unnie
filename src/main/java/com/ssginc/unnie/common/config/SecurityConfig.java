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
                          //루트는 모두 허용
                          //.requestMatchers("/", "/css/**", "/js/**", "/img/**", "/assets/**").permitAll() //첫페이지는 누구나 접근 가능, 정적 리소스 접근 허용, 싱글톤빈으로 static주소 접근 제어 가능.
                          //회원 관련 모두 허용
                          //.requestMatchers("/api/member/**").permitAll()
//                        // 마이페이지 관련 - 로그인한 사용자만 접근
//                        .requestMatchers("/api/community/board/**").authenticated()
//                        //관리자 전용 - ADMIN 권한 필요
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        // 마이페이지 (업체 관리) - 업체 담당자만 접근
//                        .requestMatchers("/api/mypage/shop/management/**").hasAnyRole("MANAGER") //Mypage 업체관리 페이지는 업체담당자(MANAGER)만 접근 가능
//                        //.anyRequest().authenticated() //모두 인증필요
                        .anyRequest().permitAll() // 그 외 모든 요청은 기본적으로 허용
                )
                .formLogin(form -> form.disable())
////                login -> login
//                        .loginPage("/member/login")
//                        .defaultSuccessUrl("/",true)
//                        .permitAll())
//                .formLogin(form -> form
//                .loginPage("/loginTest.html") // 로그인 폼 페이지
//                .permitAll())

                        .logout(logout -> logout.disable()
//                logout -> logout
//                        .logoutUrl("/member/logout")
//                        .logoutSuccessUrl("/")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID", "accessToken")
//                        .permitAll()
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
