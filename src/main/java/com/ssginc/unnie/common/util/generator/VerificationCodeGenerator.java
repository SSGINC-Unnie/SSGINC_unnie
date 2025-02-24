package com.ssginc.unnie.common.util.generator;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * 난수를 생성하는 클래스
 * 인증번호(숫자 6자리) 생성
 */
@Component
public class VerificationCodeGenerator {

    // 난수 6자리 인증번호
    private final int numLen = 6;
    //SecureRandom 객체
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateVerificationCode() {
        int upperBound = (int) Math.pow(10, numLen); //상한값(10^numLength, 10^6)
        int randomNumber = secureRandom.nextInt(upperBound); //0 ~ 999,999 난수 생성
        return String.format("%0" + numLen + "d", randomNumber); //6자리 맞춤 + 0패딩
    }
}
