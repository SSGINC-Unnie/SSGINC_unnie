package com.ssginc.unnie.admin.dto.member;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminMemberDetailResponse {
    private Long memberId;        // 회원 번호
    private String memberEmail; // 이메일 (아이디)
    private String memberName; // 이름
    private String memberNickname; // 닉네임
    private String memberPhone; // 전화번호
    private int memberState; // 회원 상태 (0: 활성, 1: 정지, 2: 탈퇴)
    private String memberRole; // 회원 권한 ADMIN:관리자 / USER: 일반 / MANAGER:업체
    private LocalDateTime memberCreatedAt; // 가입일
}
