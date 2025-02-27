package com.ssginc.unnie.member.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieMemberFindException;
import com.ssginc.unnie.common.exception.UnnieRegisterException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.generator.VerificationCodeGenerator;
import com.ssginc.unnie.common.util.validation.MemberValidator;
import com.ssginc.unnie.member.mapper.MemberMapper;
import com.ssginc.unnie.member.service.VerificationService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoEmptyResponseException;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 회원 본인인증 및 아이디/비밀번호 찾기 서비스 구현체.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final MemberValidator memberValidator; // 유효성 검사
    private final MemberMapper memberMapper;
    private final JavaMailSender mailSender;       // 이메일 전송
    private final VerificationCodeGenerator verificationCodeGenerator; //인증번호(숫자 6자리) 생성
    private final PasswordEncoder passwordEncoder; //비밀번호 암호화

    //이메일 인증
    @Value("${spring.mail.username}")
    private String fromEmail; // 발신자 이메일

    //휴대전화 인증
    @Value("${coolsms.api.key}")
    private String smsApiKey; // CoolSMS API Key

    @Value("${coolsms.api.secret}")
    private String smsApiSecret; // CoolSMS API Secret

    @Value("${coolsms.api.number}")
    private String smsSenderPhone; // 발신자 전화번호

    // 인증번호 저장
    private final ConcurrentHashMap<String, String> verificationCode = new ConcurrentHashMap<>();
    // 인증 완료 여부 저장을 위한 Map
    private final ConcurrentHashMap<String, Boolean> verificationStatus = new ConcurrentHashMap<>();

    // 이메일 인증번호 발급 및 전송
    @Override
    public String sendEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UnnieRegisterException(ErrorCode.EMAIL_INPUT_EMPTY);
        }

        //이메일 형식 검증
        memberValidator.validateEmail(email);

        //이메일 중복 검증
        if (memberMapper.checkMemberEmail(email) > 0) {
            throw new UnnieRegisterException(ErrorCode.EMAIL_ALREADY_EXISTS); // 이메일 중복 검증
        }

        String verificationNum = verificationCodeGenerator.generateVerificationCode(); // 6자리 인증번호 생성
        verificationCode.put(email, verificationNum); //인증번호 메모리에 저장

        // 이메일 전송
        SimpleMailMessage message = new SimpleMailMessage(); //메시지 생성
        message.setFrom(fromEmail); //발신인
        message.setTo(email); //수신인
        message.setSubject("[unnie] 회원가입 인증번호입니다."); //제목

        String content = "인증번호: [ " + verificationNum + " ]\n\n" +
                "이메일 인증 절차에 따라 이메일 인증번호를 발급해드립니다.\n" +
                "인증번호는 이메일 발송 시점으로부터 3분동안 유효합니다.";
        message.setText(content); //내용

        try {
            mailSender.send(message); // 이메일 전송
            log.info("인증번호 전송 완료 - email: {}, code: {}", email, verificationNum);
        } catch (MailException e) {
            throw new UnnieRegisterException(ErrorCode.EMAIL_SEND_FAILED);
        }
        return email;
    }


    //입력된 이메일 인증번호 검증
    @Override
    public boolean verifyEmailCode(String email, String code) {
        String storedCode = verificationCode.get(email); // 메모리에서 코드 조회

        if (storedCode != null && storedCode.equals(code)) {
            verificationCode.remove(email); // 인증 성공 시 제거
            verificationStatus.put(email, true);    // 인증 완료 상태 저장
            log.info("인증 성공 - email: {}", email);
            return true;
        }

        log.info("인증 실패 - email: {}, 입력 코드: {}", email, code);
        throw new UnnieRegisterException(ErrorCode.EMAIL_VERIFICATION_FAILED);
    }

    // 전화번호 인증번호 발급 및 전송
    @Override
    public String sendPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new UnnieRegisterException(ErrorCode.PHONE_INPUT_EMPTY);
        }

        //전화번호 형식 검증
        memberValidator.validatePhone(phone);

        // 수신 전화번호의 하이픈 제거
        String phoneNum = phone.replace("-", "");

        //인증번호 생성 및 저장
        String verificationNum = verificationCodeGenerator.generateVerificationCode(); // 6자리 인증번호 생성
        verificationCode.put(phoneNum, verificationNum); //인증번호 메모리에 저장

        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(smsApiKey, smsApiSecret, "https://api.coolsms.co.kr");

        //메시지 생성 및 내용 설정
        Message message = new Message();
        message.setFrom(smsSenderPhone); // 발신 번호 설정
        message.setTo(phoneNum); // 수신 번호 설정
        message.setText(
                "[unnie] 인증번호는 [ " + verificationNum + " ]입니다."
        );

        try {
            messageService.send(message); // SMS 전송
            log.info("인증번호 전송 완료 - 수신 번호: {}, 인증 코드: {}", phoneNum, verificationNum);
        } catch (NurigoMessageNotReceivedException | NurigoEmptyResponseException | NurigoUnknownException e) {
            log.error("SMS 전송 실패 - phone: {}, 이유: {}", phoneNum, e.getMessage());
            throw new UnnieRegisterException(ErrorCode.SMS_SEND_FAILED); // SMS 전송 실패 시 예외
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생 - 수신 번호: {}, 이유: {}", phoneNum, e.getMessage());
            throw new UnnieRegisterException(ErrorCode.UNKNOWN_ERROR); // 기타 예외 처리
        }
        return phone;
    }

    //입력된 전화번호 인증번호 검증
    @Override
    public boolean verifyPhoneCode(String phone, String code) {
        String phoneNum = phone.replace("-", "");
        String storedCode = verificationCode.get(phoneNum); // 메모리에서 코드 조회

        if (storedCode != null && storedCode.equals(code)) {
            verificationCode.remove(phoneNum); // 인증 성공 시 제거
            verificationStatus.put(phoneNum, true);    // 인증 완료 상태 저장
            log.info("인증 성공 - 전화번호: {}", phoneNum);
            return true;
        }

        log.info("인증 실패 - 전화번호: {}, 입력 코드: {}", phoneNum, code);
        throw new UnnieRegisterException(ErrorCode.PHONE_VERIFICATION_FAILED);
    }

    // 인증 여부 확인 메서드
    @Override
    public boolean isVerified(String key) {
        return verificationStatus.getOrDefault(key, false); // 인증 여부 반환 (없으면 false)
    }

    //아이디(이메일) 찾기
    @Override
    public String findId(String memberName, String memberPhone) {
        memberValidator.validateName(memberName);  // 이름 유효성 검사
        memberValidator.validatePhone(memberPhone); // 전화번호 유효성 검사

        String email = memberMapper.selectMemberByNameAndPhone(memberName, memberPhone);
        if (email == null || email.trim().isEmpty()) {
            log.info("아이디 찾기 실패 - 이름: {}, 전화번호: {}", memberName, memberPhone);
            throw new UnnieMemberFindException(ErrorCode.MEMBER_NOT_FOUND);
        }

        log.info("아이디 찾기 성공 - 이름: {}, 전화번호: {}, 이메일: {}", memberName, memberPhone, email);
        return email;
    }

    //비밀번호 찾기 및 임시 비밀번호 발급
    @Override
    public void findPassword(String memberEmail) {
        memberValidator.validateEmail(memberEmail);  // 이메일 유효성 검사

        // 이메일 존재 여부 확인
        if (memberMapper.checkMemberEmail(memberEmail) == 0) {
            log.warn("[비밀번호 찾기 실패] 존재하지 않는 이메일: {}", memberEmail);
            throw new UnnieMemberFindException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 6자리 임시 비밀번호 생성 및 암호화
        //임시 비밀번호
        String tempPassword = verificationCodeGenerator.generateVerificationCode();
        //암호화
        String encryptedPassword = passwordEncoder.encode(tempPassword);

        // 비밀번호 업데이트
        int updatedRows = memberMapper.updateMemberPassword(memberEmail, encryptedPassword);
        if (updatedRows == 0) {
            log.error("[비밀번호 업데이트 실패] 이메일: {}", memberEmail);
            throw new UnnieMemberFindException(ErrorCode.PASSWORD_UPDATE_FAILED);
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(memberEmail);
        message.setSubject("[unnie] 임시 비밀번호 발급 안내");
        message.setText("임시 비밀번호: " + tempPassword + "\n로그인 후 반드시 비밀번호를 변경해 주세요.");

        try {
            mailSender.send(message);
            log.info("[임시 비밀번호 전송 완료] 이메일: {}", memberEmail);
        } catch (MailException e) {
            log.error("[임시 비밀번호 전송 실패] 이메일: {}", memberEmail, e);
            throw new UnnieMemberFindException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}
