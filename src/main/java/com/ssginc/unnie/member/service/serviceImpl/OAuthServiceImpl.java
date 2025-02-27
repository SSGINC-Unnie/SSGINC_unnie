package com.ssginc.unnie.member.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieRegisterException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.Validator;
import com.ssginc.unnie.member.dto.MemberRegisterRequest;
import com.ssginc.unnie.member.mapper.MemberMapper;
import com.ssginc.unnie.member.service.OAuthService;
import com.ssginc.unnie.member.service.RegisterService;
import com.ssginc.unnie.member.service.VerificationService;
import com.ssginc.unnie.member.vo.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * OAuth 로그인 서비스 구현체.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final MemberMapper memberMapper;


    @Override
    public void insertOAuthMember(Member member) {
        memberMapper.insertOAuthMember(member);
    }

    @Override
    public Member selectMemberByEmail(String memberEmail) {
        return memberMapper.selectMemberByEmail(memberEmail);
    }
}
