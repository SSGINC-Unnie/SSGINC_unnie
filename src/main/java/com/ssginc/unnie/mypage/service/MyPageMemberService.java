package com.ssginc.unnie.mypage.service;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.member.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * 마이페이지 회원정보 수정 및 탈퇴 관련 인터페이스
 */
public interface MyPageMemberService {

    // 회원 정보 조회
    MyPageMemberResponse findById(Long memberId);

    //프로필 사진 수정
    int updateProfile(MyPageProfileImgUpdateRequest profileImgUpdateRequest, MultipartFile file);

    //프로필 기본 이미지 적용
    int updateDefaultProfile(MyPageProfileImgUpdateRequest profileImgUpdateRequest);

    // 닉네임 수정
    ResponseDto<Map<String, String>> updateNickname(MyPageNicknameUpdateRequest nicknameUpdateRequest, HttpServletResponse response);

    // 전화번호 수정
    int updatePhone(MyPagePhoneUpdateRequest phoneUpdateRequest);

    // 비밀번호 수정
    int updatePassword(MyPagePwUpdateRequest pwUpdateRequest);

    //회원 탈퇴
    int withdrawMember(MyPageWithdrawRequest withdrawRequest);

    //프로필 사진 파일 경로 설정
    String saveFile(MultipartFile file);
}