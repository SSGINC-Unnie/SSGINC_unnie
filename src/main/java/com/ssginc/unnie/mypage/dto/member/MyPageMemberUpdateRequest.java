package com.ssginc.unnie.mypage.dto.member;

import lombok.Data;

/**
 * 회원정보 수정 DTO
 */
@Data
public class MyPageMemberUpdateRequest {
    private Long memberId;         // 회원 ID
    private String memberNickname; // 닉네임
    private String memberPhone; // 전화번호
    private String memberProfile; // 프로필 이미지

    private String currentPw;     // 현재 비밀번호
    private String newPw;         // 새 비밀번호
    private String newPwConfirm;  // 새 비밀번호 확인
    private String memberPw;
}
