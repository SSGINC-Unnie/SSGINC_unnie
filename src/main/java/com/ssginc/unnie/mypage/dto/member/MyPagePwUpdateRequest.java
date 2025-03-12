package com.ssginc.unnie.mypage.dto.member;

import lombok.Data;

/**
 * 비밀번호 수정 DTO
 */
@Data
public class MyPagePwUpdateRequest {
    private Long memberId;    // 회원 ID

    private String currentPw; // 현재 비밀번호
    private String newPw;     // 새 비밀번호
    private String newPwConfirm; // 새 비밀번호 확인

    private String memberPw;
}
