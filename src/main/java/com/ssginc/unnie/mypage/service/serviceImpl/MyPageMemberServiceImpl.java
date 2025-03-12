package com.ssginc.unnie.mypage.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieMediaException;
import com.ssginc.unnie.common.exception.UnnieMemberException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.MemberValidator;
import com.ssginc.unnie.member.vo.Member;
import com.ssginc.unnie.mypage.dto.member.*;
import com.ssginc.unnie.mypage.mapper.MyPageMemberMapper;
import com.ssginc.unnie.mypage.service.MyPageMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 마이페이지 회원정보 수정 및 탈퇴 인터페이스 구현 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageMemberServiceImpl implements MyPageMemberService {

    private final MyPageMemberMapper myPageMemberMapper;
    private final PasswordEncoder passwordEncoder;
    private final MemberValidator memberValidator;

    @Override
    public MyPageMemberResponse findById(Long memberId) {
        return myPageMemberMapper.findById(memberId);
    }

    /**
     * 프로필 사진 수정
     */
    @Override
    public int updateProfile(MyPageProfileImgUpdateRequest profileImgUpdateRequest, MultipartFile file) {
        String fileUrl = saveFile(file);
        profileImgUpdateRequest.setMemberProfile(fileUrl);
        int res = myPageMemberMapper.updateProfile(profileImgUpdateRequest);
        if (res == 0){
            throw new UnnieMediaException(ErrorCode.FILE_NOT_FOUND);
        }
        return res;
    }

    /**
     * 닉네임 수정
     */
    @Override
    public int updateNickname(MyPageNicknameUpdateRequest nicknameUpdateRequest) {

        // 닉네임 유효성 검증
        if (nicknameUpdateRequest.getMemberNickname() != null && !nicknameUpdateRequest.getMemberNickname().trim().isEmpty()) {
            memberValidator.validateNickname(nicknameUpdateRequest.getMemberNickname());
        }

        //닉네임 중복 체크
        if (myPageMemberMapper.countByNickname(nicknameUpdateRequest.getMemberNickname(),nicknameUpdateRequest.getMemberId()) > 0) {
            throw new UnnieMemberException(ErrorCode.DUPLICATE_NICKNAME);
        }
        return myPageMemberMapper.updateNickname(nicknameUpdateRequest);
    }

    /**
     * 전화번호 수정
     */
    @Override
    public int updatePhone(MyPagePhoneUpdateRequest phoneUpdateRequest) {

        //전화번호 유효성 검증
        if (phoneUpdateRequest.getMemberPhone() != null && !phoneUpdateRequest.getMemberPhone().trim().isEmpty()) {
            memberValidator.validatePhone(phoneUpdateRequest.getMemberPhone());
        }
        return myPageMemberMapper.updatePhone(phoneUpdateRequest);
    }

    /**
     * 비밀번호 수정
     */
    @Override
    public int updatePassword(MyPagePwUpdateRequest pwUpdateRequest) {

        // 현재 회원 조회
        Member member = myPageMemberMapper.findMemberById(pwUpdateRequest.getMemberId());
        if (member == null) {
            throw new UnnieMemberException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 현재 비밀번호 일치 여부 확인
        // 입력한 현재 비밀번호와 db에 저장된 암호화 비밀번호 비교
        if (!passwordEncoder.matches(pwUpdateRequest.getCurrentPw(), member.getMemberPw())) {
            throw new UnnieMemberException(ErrorCode.INVALID_PASSWORD);
        }

        //새 비밀번호 유효성 검증
        if (pwUpdateRequest.getNewPw() != null && !pwUpdateRequest.getNewPw().trim().isEmpty()) {
            memberValidator.validatePw(pwUpdateRequest.getNewPw());
        }

        // 새 비밀번호와 새 비밀번호 확인 일치 여부
        if (!pwUpdateRequest.getNewPw().equals(pwUpdateRequest.getNewPwConfirm())) {
            throw new UnnieMemberException(ErrorCode.PASSWORD_MISMATCH);
        }

        //새 비밀번호 암호화 후 저장
        pwUpdateRequest.setMemberPw(passwordEncoder.encode(pwUpdateRequest.getNewPw()));
        int res = myPageMemberMapper.updatePassword(pwUpdateRequest);
        if (res == 0) {
            throw new UnnieMemberException(ErrorCode.MEMBER_UPDATE_FAILED);
        }
        return res;
    }

    /**
     * 회원탈퇴
     */
    @Override
    public int withdrawMember(Long memberId) {
        //회원 존재 여부 확인
        Member member = myPageMemberMapper.findMemberById(memberId);
        if (member == null) {
            throw new UnnieMemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
        //회원 상태 탈퇴(2)로 업데이트
        int res = myPageMemberMapper.withdrawMember(memberId);
        if (res == 0){
            throw new UnnieMemberException(ErrorCode.MEMBER_DELETION_FAILED);
        }
        return res;
    }

    /**
     * 프로필 사진 파일 경로 설정
     */
    @Override
    public String saveFile(MultipartFile file) {
        // 새 업로드 경로
        String uploadDir = "C:/workSpace/SSGINC_Unnie/src/main/resources/static/upload/";
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            folder.mkdirs();  // 폴더가 없으면 생성
        }

        // 원본 파일명과 안전한 파일명 생성
        String originalFileName = file.getOriginalFilename();
        String safeFileName = UUID.randomUUID().toString() + "_"
                + originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        // 최종 파일 객체 생성
        File destination = new File(folder, safeFileName);
        try {
            file.transferTo(destination);
        } catch (IOException e) {
            throw new UnnieMediaException(ErrorCode.FILE_UPLOAD_FAILED, e) {
            };
        }
        System.out.println("Saving file to: " + destination.getAbsolutePath());
        // DB에는 웹 접근 가능한 경로(/upload/파일명) 저장
        return "/upload/" + safeFileName;
    }
}

