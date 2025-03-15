package com.ssginc.unnie.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginRequest {
    private String memberEmail; // 이메일 (아이디)
    private String memberPw; // 비밀번호
}