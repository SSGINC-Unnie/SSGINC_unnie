package com.ssginc.unnie.member.mapper;

import com.ssginc.unnie.member.dto.MemberRegisterRequest;
import com.ssginc.unnie.member.vo.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 회원가입을 위한 Mapper 인터페이스
 */
@Mapper
public interface MemberMapper {
    //이메일 중복 확인
    int checkMemberEmail(String email);
    //닉네임 중복 확인
    int checkMemberNickname(String nickname);
    //회원 정보 추가
    int insertMember(MemberRegisterRequest member);
    //OAuth 회원정보 저장
    void insertOAuthMember(Member member);
    //회원 정보 조회
    Member selectMemberByEmail(String memberEmail);
    //회원번호(memberId)를 이용해 회원 정보를 조회
    Member selectMemberById(Long memberId);
    // 이름 + 전화번호로 회원 조회
    String selectMemberByNameAndPhone(@Param("memberName") String memberName, @Param("memberPhone") String memberPhone);
    // 이메일로 비밀번호 업데이트
    int updateMemberPassword(@Param("memberEmail") String memberEmail, @Param("memberPw") String memberPw);
}
