package com.ssginc.unnie.mypage.dto.member;

import lombok.Data;

/**
 * 전화번호 수정 DTO
 */
@Data
public class MyPagePhoneUpdateRequest {
    private Long memberId;         // 회원 ID
    private String memberPhone;  //전화번호
}
