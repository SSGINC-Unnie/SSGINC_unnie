package com.ssginc.unnie.member.service;

import com.ssginc.unnie.member.dto.MemberRegisterRequest;

/**
 * 회원가입 기능 인터페이스
 */
public interface RegisterService {
    //회원정보 등록
    int insertMember(MemberRegisterRequest member);
    //이메일 중복 체크
    int checkMemberEmail(String email);
    //닉네임 중복 체크
    int checkMemberNickname(String nickname);
}
