package com.ssginc.unnie.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberRegisterRequest {
    private String memberEmail; // 이메일 (아이디)
    private String memberPw; // 비밀번호
    private String memberName; // 이름
    private String memberNickname; // 닉네임
    private String memberPhone; // 전화번호
    private String memberBirth; // 생년월일
    private char memberGender; // 성별 (M/F)
}
