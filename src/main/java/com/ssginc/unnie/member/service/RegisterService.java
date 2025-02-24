package com.ssginc.unnie.member.service;

import com.ssginc.unnie.member.dto.MemberRegisterRequest;

/**
 * 회원가입 기능 서비스.
 */
public interface RegisterService {
    //회원정보 등록
    int insertMember(MemberRegisterRequest member);
}
