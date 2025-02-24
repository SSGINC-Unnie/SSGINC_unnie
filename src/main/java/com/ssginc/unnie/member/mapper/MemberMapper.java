package com.ssginc.unnie.member.mapper;

import com.ssginc.unnie.member.dto.MemberRegisterRequest;
import com.ssginc.unnie.member.vo.Member;
import org.apache.ibatis.annotations.Mapper;

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
    //회원 정보 조회
    Member selectMemberByEmail(String memberEmail);
    //회원번호(memberId)를 이용해 회원 정보를 조회
    Member selectMemberById(Long memberId);


}
