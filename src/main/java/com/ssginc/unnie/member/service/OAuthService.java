package com.ssginc.unnie.member.service;

import com.ssginc.unnie.member.dto.OAuth2Response;
import com.ssginc.unnie.member.vo.Member;

public interface OAuthService {
    // 소셜 로그인 후 회원 처리: 기존 회원 조회 및 새 회원 등록 처리
    Member handleOAuthLogin(OAuth2Response oAuth2Response);
}
