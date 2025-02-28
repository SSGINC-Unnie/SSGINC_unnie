package com.ssginc.unnie.member.dto;

public interface OAuth2Response {
    String getProvider(); // 구글, 네이버 등 로그인 제공자
    String getProviderId(); // 소셜 로그인 고유 ID (구글: 'sub')
    String getEmail(); //소셜 로그인 이메일
    String getName(); // 사용자 이름
}
