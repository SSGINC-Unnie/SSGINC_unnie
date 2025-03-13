package com.ssginc.unnie.mypage.dto.member;

import lombok.Data;

/**
 * 회원탈퇴 DTO
 */
@Data
public class MyPageWithdrawRequest {
    private Long memberId;    // 회원 ID
    private String currentPw; // 현재 비밀번호
}
