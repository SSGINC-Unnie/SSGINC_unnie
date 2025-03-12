package com.ssginc.unnie.mypage.service;

import com.ssginc.unnie.mypage.dto.member.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 마이페이지 회원정보 수정 및 탈퇴 관련 인터페이스
 */
public interface MyPageMemberService {

    // 회원 정보 조회
    MyPageMemberResponse findById(Long memberId);

    //프로필 사진 수정
    int updateProfile(MyPageProfileImgUpdateRequest profileImgUpdateRequest, MultipartFile file);

    // 닉네임 수정
    int updateNickname(MyPageNicknameUpdateRequest nicknameUpdateRequest);

    // 전화번호 수정
    int updatePhone(MyPagePhoneUpdateRequest phoneUpdateRequest);

    // 비밀번호 수정
    int updatePassword(MyPagePwUpdateRequest pwUpdateRequest);

    //회원 탈퇴
    int withdrawMember(Long memberId);

    //프로필 사진 파일 경로 설정
    String saveFile(MultipartFile file);
}
