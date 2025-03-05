package com.ssginc.unnie.mypage.mapper;

import com.ssginc.unnie.member.vo.Member;
import com.ssginc.unnie.mypage.dto.member.MyPageMemberResponse;
import com.ssginc.unnie.mypage.dto.member.MyPageMemberUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MyPageMemberMapper {
    //회원 번호(memberId)를 기준으로 회원 정보를 조회
    MyPageMemberResponse findById(Long memberId);
    Member findMemberById(Long memberId);
    //현재 회원(memberId)을 제외한 동일한 닉네임을 가진 회원의 수
    int countByNickname(@Param("nickname") String nickname,
                       @Param("memberId") Long memberId);
    //회원 정보 업데이트
    int updateMember(MyPageMemberUpdateRequest updateRequest);
    //회원 탈퇴
    int withdrawMember(Long memberId);
    
}
