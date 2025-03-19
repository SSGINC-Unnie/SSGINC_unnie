package com.ssginc.unnie.mypage.mapper;

import com.ssginc.unnie.member.vo.Member;
import com.ssginc.unnie.mypage.dto.member.*;
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

    //프로필 사진 수정
    int updateProfile(MyPageProfileImgUpdateRequest profileImgUpdateRequest);

    //프로필 기본 이미지 적용
    int updateDefaultProfile(MyPageProfileImgUpdateRequest profileImgUpdateRequest);

    // 닉네임 수정
    int updateNickname(MyPageNicknameUpdateRequest nicknameUpdateRequest);

    // 전화번호 수정
    int updatePhone(MyPagePhoneUpdateRequest phoneUpdateRequest);

    // 비밀번호 수정
    int updatePassword(MyPagePwUpdateRequest pwUpdateRequest);

    //회원 탈퇴
    int withdrawMember(Long memberId);
}
