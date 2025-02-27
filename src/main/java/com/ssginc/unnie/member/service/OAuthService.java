package com.ssginc.unnie.member.service;

import com.ssginc.unnie.member.dto.OAuth2Response;
import com.ssginc.unnie.member.vo.Member;

public interface OAuthService {

    void insertOAuthMember(Member member);
    Member selectMemberByEmail(String memberEmail);
}
