package com.ssginc.unnie.mypage.dto.member;

import lombok.Data;

/**
 * 닉네임 수정 DTO
 */
@Data
public class MyPageNicknameUpdateRequest {
    private Long memberId;         // 회원 ID
    private String memberNickname; // 닉네임
}
