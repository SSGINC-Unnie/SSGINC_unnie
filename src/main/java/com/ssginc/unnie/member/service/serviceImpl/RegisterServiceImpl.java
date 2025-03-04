package com.ssginc.unnie.member.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieRegisterException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.Validator;
import com.ssginc.unnie.member.dto.MemberRegisterRequest;
import com.ssginc.unnie.member.mapper.MemberMapper;
import com.ssginc.unnie.member.service.RegisterService;
import com.ssginc.unnie.member.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 회원 가입 인터페이스의 구현 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final Validator<MemberRegisterRequest> memberRequestValidator; //유효성 검사
    private final VerificationService verificationService; // 인증 서비스

    @Override
    public int insertMember(MemberRegisterRequest member) {
        //유효성 검사
        memberRequestValidator.validate(member);

        //이메일 중복 체크
        if(memberMapper.checkMemberEmail(member.getMemberEmail()) > 0) {
            throw new UnnieRegisterException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(member.getMemberPw());
        member.setMemberPw(encodedPassword);

        //닉네임 중복 체크
        if(memberMapper.checkMemberNickname(member.getMemberNickname()) > 0) {
            throw new UnnieRegisterException(ErrorCode.DUPLICATE_NICKNAME);
        }

        //회원정보 등록
        int result = memberMapper.insertMember(member);
        if (result != 1) {
            throw new UnnieRegisterException(ErrorCode.MEMBER_REGISTRATION_FAILED);
        }
        return result;
    }
}
