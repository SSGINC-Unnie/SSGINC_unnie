package com.ssginc.unnie.admin.service;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.admin.dto.member.AdminMemberDetailResponse;
import com.ssginc.unnie.admin.dto.member.AdminMemberResponse;

/**
 * 관리자페이지 회원관리 인터페이스
 */
public interface AdminMemberService {
    PageInfo<AdminMemberResponse> findAllMembers(int page, int pageSize);
    AdminMemberDetailResponse findMemberDetail(int memberId);
}
