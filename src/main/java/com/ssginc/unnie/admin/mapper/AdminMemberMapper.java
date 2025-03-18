package com.ssginc.unnie.admin.mapper;

import com.ssginc.unnie.admin.dto.member.AdminMemberDetailResponse;
import com.ssginc.unnie.admin.dto.member.AdminMemberResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMemberMapper {
    //회원 전체 리스트 조회
    List<AdminMemberResponse> findAllMembers();
    //회원 상세정보 조회
    AdminMemberDetailResponse findMemberDetail(int memberId);


}