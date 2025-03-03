package com.ssginc.unnie.mypage.service;

import com.ssginc.unnie.mypage.dto.member.MyPageMemberResponse;
import com.ssginc.unnie.mypage.dto.member.MyPageMemberUpdateRequest;

/**
 * 마이페이지 회원정보 조회 및 수정 관련 인터페이스
 */
public interface MyPageMemberService {

    //회원 정보 수정
    int updateMember(MyPageMemberUpdateRequest updateRequest, Long memberId);
}
