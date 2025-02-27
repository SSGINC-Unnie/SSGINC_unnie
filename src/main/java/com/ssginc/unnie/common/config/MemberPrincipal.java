package com.ssginc.unnie.common.config;

import com.ssginc.unnie.member.dto.OAuth2Response;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.ssginc.unnie.member.vo.Member;
import org.springframework.security.oauth2.core.user.OAuth2User;


import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * MemberPrincipal은 Member 엔티티를 감싸서 스프링 시큐리티에서 사용하는
 * UserDetails 인터페이스를 구현하는 커스텀 클래스입니다.
 */
public class MemberPrincipal implements UserDetails {

    private final Member member;


    //일반 로그인
    public MemberPrincipal(Member member) {
        this.member = member;
    }

    @Override
    public String getPassword() {
        return member.getMemberPw();
    }

    @Override
    public String getUsername() {
        return member.getMemberEmail(); // 이메일 반환
    }

    /**
     * 회원의 역할 정보를 스프링 시큐리티 권한(Authority)으로 변환
     * 예: ROLE_ADMIN, ROLE_USER, ROLE_MANAGER 등
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> member.getMemberRole());
    }

    public Long getMemberId() {
        return member.getMemberId();
    }

    public String getMemberNickname() {
        return member.getMemberNickname(); // Member 엔티티의 닉네임 반환
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Member getMember() {
        return this.member;
    }
}