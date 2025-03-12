package com.ssginc.unnie.mypage.dto.member;

import lombok.Data;

/**
 * 닉네임 수정 DTO
 */
@Data
public class MyPageProfileImgUpdateRequest {
    private Long memberId;         // 회원 ID
    private String memberProfile; // 프로필 이미지
}
