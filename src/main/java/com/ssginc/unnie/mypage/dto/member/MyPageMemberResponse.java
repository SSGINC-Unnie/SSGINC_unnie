package com.ssginc.unnie.mypage.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원정보 조회 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageMemberResponse {
    private Long memberId;        // 회원 번호
    private String memberEmail; // 이메일 (아이디)
    private String memberName; // 이름
    private String memberNickname; // 닉네임
    private String memberPhone; // 전화번호
    private String memberProfile; // 프로필 이미지
}
