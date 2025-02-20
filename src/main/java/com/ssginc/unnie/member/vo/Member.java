package com.ssginc.unnie.member.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    private long memberId; // 회원 번호
    private String memberEmail; // 이메일 (아이디)
    private String memberPw; // 비밀번호
    private String memberName; // 이름
    private String memberNickname; // 닉네임
    private String memberPhone; // 전화번호
    private String memberBirth; // 생년월일
    private char memberGender; // 성별 (M/F)
    private String memberProfile; // 프로필 이미지
    private int memberRole; // 회원 권한 (0: 관리자, 1: 일반, 2: 업체)
    private int memberState; // 회원 상태 (0: 활성, 1: 정지, 2: 탈퇴)
    private LocalDateTime memberCreatedAt; // 가입일
    private LocalDateTime memberUpdatedAt; // 수정일
}
