package com.ssginc.unnie.common.util.validation;


import com.ssginc.unnie.common.exception.UnnieMemberException;
import com.ssginc.unnie.common.exception.UnnieRegisterException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.member.dto.MemberRegisterRequest;
import com.ssginc.unnie.mypage.dto.member.MyPageMemberUpdateRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 회원 정보 검증 클래스.
 */
@Component
public class MemberValidator implements Validator<MemberRegisterRequest>{

    private final String emailRegex;    // 아이디(이메일) 정규식
    private final String pwRegex;       // 비밀번호 정규식
    private final String nameRegex;     // 이름 정규식
    private final String nicknameRegex; // 닉네임 정규식
    private final String phoneRegex;    // 전화번호 정규식
//    private final String birthRegex;    // 생년월일 정규식

    public MemberValidator() {
        emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        pwRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$";
        nameRegex = "^[가-힣]{2,10}$";
        nicknameRegex = "^[가-힣a-zA-Z0-9]{2,20}$";
        phoneRegex = "^01[0-9]\\d{7,8}$";
//        birthRegex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";
    }

    /**
     * 회원가입 유효성 검사
     */
    @Override
    public boolean validate(MemberRegisterRequest object) {
        if (object == null) {
            throw new UnnieRegisterException(ErrorCode.NULL_POINTER_ERROR);
        }
        validateEmail(object.getMemberEmail());
        validatePw(object.getMemberPw());
        validateName(object.getMemberName());
        validateNickname(object.getMemberNickname());
        validatePhone(object.getMemberPhone());
//        validateBirth(object.getMemberBirth());
        return true;
    }

    // 아이디(이메일) 유효성 검사
    public void validateEmail(String email) {
        validateValue(email, emailRegex,ErrorCode.INVALID_EMAIL_FORMAT);
    }

    //비밀번호 유효성 검사
    public void validatePw(String pw){
        validateValue(pw,pwRegex,ErrorCode.INVALID_PASSWORD_FORMAT);
    }

    //이름 유효성 검사
    public void validateName(String name){
        validateValue(name,nameRegex,ErrorCode.INVALID_NAME_FORMAT);
    }

    //닉네임 유효성 검사
    public void validateNickname(String nickname){
        validateValue(nickname,nicknameRegex,ErrorCode.INVALID_NICKNAME_FORMAT);
    }

    //전화번호 유효성 검사
    public void validatePhone(String phone){
        validateValue(phone,phoneRegex,ErrorCode.INVALID_PHONE_FORMAT);
    }

    //생년월일 유효성 검사
//    public void validateBirth(String birth){
//        validateValue(birth,birthRegex,ErrorCode.INVALID_BIRTH_FORMAT);
//
//        //날짜 유효성 검증
//        LocalDate birthDate = LocalDate.parse(birth); //yyyy-MM-dd 형식 파싱
//        LocalDate today = LocalDate.now();
//
//        if (birthDate.isAfter(today) || birthDate.isBefore(today.minusYears(150))) {
//            throw new UnnieRegisterException(ErrorCode.INVALID_BIRTH_FORMAT);
//        }
//    }

    //공통 정규식 검증
    private void validateValue(String value, String regex, ErrorCode errorCode) {
        if (value == null || !value.matches(regex)){
            throw new UnnieRegisterException(errorCode);
        }
    }

    /**
     * 회원정보 수정 유효성 검사
     * 닉네임, 전화번호, 비밀번호 : 값이 있으면 유효성 검사
     */
    public boolean validateUpdateRequest(MyPageMemberUpdateRequest request) {
        if (request == null) {
            throw new UnnieMemberException(ErrorCode.NULL_POINTER_ERROR);
        }

        if (request.getMemberNickname() != null && !request.getMemberNickname().trim().isEmpty()) {
            validateNickname(request.getMemberNickname());
        }

        if (request.getMemberPhone() != null && !request.getMemberPhone().trim().isEmpty()) {
            validatePhone(request.getMemberPhone());
        }

        if (request.getNewPw() != null && !request.getNewPw().trim().isEmpty()) {
            validatePw(request.getNewPw());
        }
        return true;
    }
}
